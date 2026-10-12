package ru.akvine.istochnik.services.schema;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import ru.akvine.istochnik.enums.SchemaType;
import ru.akvine.istochnik.exceptions.SchemaConversionException;

/**
 * Читает таблицы из журнала изменений Liquibase в формате XML.
 * <p>
 * Основа описания таблицы - изменение createTable. Из остальных учитываются те, что меняют колонки и ключи
 * уже созданных в этом журнале таблиц: addColumn, dropColumn, addPrimaryKey, addUniqueConstraint,
 * addForeignKeyConstraint, addNotNullConstraint, dropNotNullConstraint и уникальный createIndex.
 * Подключаемые журналы (include, includeAll) не читаются: сервис не имеет доступа к файлам.
 */
@Component
public class XmlSchemaParser implements SchemaParser {
    private static final String DISALLOW_DOCTYPE_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";

    // Откат и предусловия описывают не структуру таблиц, их содержимое не читается
    private static final Set<String> SKIPPED_ELEMENTS = Set.of("rollback", "preConditions");

    // Изменения лежат в журнале не глубже нескольких уровней (databaseChangeLog, changeSet, изменение).
    // Предел не даёт документу с очень глубокой вложенностью переполнить стек при обходе
    private static final int MAX_DEPTH = 16;

    private static final String NAMES_SEPARATOR = ",";
    private static final String TRUE = "true";
    private static final String FALSE = "false";

    @Override
    public ParsedSchema parse(String content) {
        SchemaBuilder builder = new SchemaBuilder();
        visit(readDocument(content).getDocumentElement(), builder, 0);
        return builder.build();
    }

    @Override
    public SchemaType getType() {
        return SchemaType.XML;
    }

    private void visit(Element element, SchemaBuilder builder, int depth) {
        String name = element.getLocalName();
        if (depth > MAX_DEPTH || SKIPPED_ELEMENTS.contains(name)) {
            return;
        }

        switch (name) {
            case "createTable" -> createTable(element, builder);
            case "addColumn" -> addColumns(tableName(element, "tableName"), element, builder);
            case "dropColumn" -> dropColumns(element, builder);
            case "addPrimaryKey" -> builder.primaryKey(
                    tableName(element, "tableName"), names(element.getAttribute("columnNames")));
            case "addUniqueConstraint" -> builder.unique(
                    tableName(element, "tableName"), names(element.getAttribute("columnNames")));
            case "addForeignKeyConstraint" -> builder.foreignKey(
                    tableName(element, "baseTableName"),
                    names(element.getAttribute("baseColumnNames")),
                    reference(
                            element.getAttribute("referencedTableName"),
                            element.getAttribute("referencedColumnNames")));
            case "addNotNullConstraint" -> builder.notNull(
                    tableName(element, "tableName"), element.getAttribute("columnName"), true);
            case "dropNotNullConstraint" -> builder.notNull(
                    tableName(element, "tableName"), element.getAttribute("columnName"), false);
            case "createIndex" -> createIndex(element, builder);
            case "include", "includeAll" -> builder.warn(String.format(
                    "Included change log [%s] is not read",
                    StringUtils.firstNonBlank(element.getAttribute("file"), element.getAttribute("path"))));
            default -> children(element).forEach(child -> visit(child, builder, depth + 1));
        }
    }

    private void createTable(Element element, SchemaBuilder builder) {
        String tableName = tableName(element, "tableName");
        if (StringUtils.isNotBlank(tableName)) {
            builder.createTable(tableName);
            addColumns(tableName, element, builder);
        }
    }

    private void addColumns(String tableName, Element element, SchemaBuilder builder) {
        for (Element column : columns(element)) {
            if (StringUtils.isNotBlank(column.getAttribute("name"))) {
                builder.addColumn(tableName, toColumnSchema(column));
            }
        }
    }

    private ColumnSchema toColumnSchema(Element column) {
        ColumnSchema schema = new ColumnSchema()
                .setName(column.getAttribute("name"))
                .setType(ColumnTypeReader.parse(column.getAttribute("type")))
                .setAutoIncrement(isTrue(column, "autoIncrement"));

        Element constraints = constraints(column);
        if (constraints != null) {
            boolean primaryKey = isTrue(constraints, "primaryKey");
            schema.setNotNull(primaryKey || FALSE.equalsIgnoreCase(constraints.getAttribute("nullable")));
            schema.setUnique(primaryKey || isTrue(constraints, "unique"));
            // Внешний ключ записывается либо одной строкой roles(id), либо таблицей и колонками по отдельности
            schema.setReference(StringUtils.firstNonBlank(
                    constraints.getAttribute("references"),
                    reference(
                            constraints.getAttribute("referencedTableName"),
                            constraints.getAttribute("referencedColumnNames"))));
        }
        return schema;
    }

    // Колонки задаются либо атрибутом columnName, либо вложенными элементами column
    private void dropColumns(Element element, SchemaBuilder builder) {
        String tableName = tableName(element, "tableName");
        List<String> columnNames = new ArrayList<>(names(element.getAttribute("columnName")));
        columns(element).forEach(column -> columnNames.add(column.getAttribute("name")));
        columnNames.forEach(columnName -> builder.dropColumn(tableName, columnName));
    }

    private void createIndex(Element element, SchemaBuilder builder) {
        if (isTrue(element, "unique")) {
            List<String> columnNames = columns(element).stream()
                    .map(column -> column.getAttribute("name"))
                    .toList();
            builder.unique(tableName(element, "tableName"), columnNames);
        }
    }

    private String tableName(Element element, String attribute) {
        String schemaAttribute = "baseTableName".equals(attribute) ? "baseTableSchemaName" : "schemaName";
        String schemaName = element.getAttribute(schemaAttribute);
        String tableName = element.getAttribute(attribute);
        return StringUtils.isBlank(schemaName) ? tableName : schemaName + "." + tableName;
    }

    private String reference(String tableName, String columnNames) {
        if (StringUtils.isBlank(tableName)) {
            return null;
        }
        return StringUtils.isBlank(columnNames) ? tableName : tableName + "(" + columnNames + ")";
    }

    private List<String> names(String commaSeparated) {
        return Arrays.stream(commaSeparated.split(NAMES_SEPARATOR))
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
                .toList();
    }

    private boolean isTrue(Element element, String attribute) {
        return TRUE.equalsIgnoreCase(element.getAttribute(attribute));
    }

    private List<Element> columns(Element element) {
        return children(element).stream()
                .filter(child -> "column".equals(child.getLocalName()))
                .toList();
    }

    private Element constraints(Element column) {
        return children(column).stream()
                .filter(child -> "constraints".equals(child.getLocalName()))
                .findFirst()
                .orElse(null);
    }

    private List<Element> children(Element element) {
        List<Element> children = new ArrayList<>();
        for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof Element childElement) {
                children.add(childElement);
            }
        }
        return children;
    }

    private Document readDocument(String content) {
        try {
            DocumentBuilder documentBuilder = createDocumentBuilderFactory().newDocumentBuilder();
            documentBuilder.setErrorHandler(new RethrowingErrorHandler());
            return documentBuilder.parse(new InputSource(new StringReader(content)));
        } catch (SAXException | IOException | ParserConfigurationException exception) {
            throw new SchemaConversionException("Schema is not a valid XML: " + exception.getMessage(), exception);
        }
    }

    // Схема приходит от клиента: объявления DOCTYPE и внешние сущности запрещены, чтобы через неё нельзя было
    // прочитать файлы сервера или обратиться в сеть (XXE)
    private DocumentBuilderFactory createDocumentBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature(DISALLOW_DOCTYPE_FEATURE, true);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        // Журналы Liquibase объявляют пространство имён, элементы сравниваются по локальным именам
        factory.setNamespaceAware(true);
        return factory;
    }

    // Обработчик по умолчанию пишет ошибки разбора в stderr, здесь они только пробрасываются
    private static final class RethrowingErrorHandler implements ErrorHandler {
        @Override
        public void warning(SAXParseException exception) {}

        @Override
        public void error(SAXParseException exception) throws SAXException {
            throw exception;
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            throw exception;
        }
    }
}
