package ru.akvine.istochnik.services.schema;

import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Component;
import ru.akvine.istochnik.enums.SchemaType;

/**
 * Читает таблицы из SQL-скрипта. Операторы должны разделяться точкой с запятой.
 * <p>
 * Основа описания таблицы - оператор CREATE TABLE. Из остальных операторов учитываются только те, что меняют
 * колонки и ключи уже созданных в этом скрипте таблиц: ALTER TABLE (ADD и DROP COLUMN, ключи, SET и DROP NOT NULL),
 * CREATE UNIQUE INDEX и CREATE TYPE ... AS ENUM. Всё остальное пропускается.
 */
@Component
public class SqlSchemaParser implements SchemaParser {
    // CREATE [GLOBAL | LOCAL] [TEMPORARY | TEMP] [UNLOGGED] TABLE, CREATE OR REPLACE TABLE, CREATE CACHED TABLE
    private static final Set<String> TABLE_MODIFIERS =
            Set.of("GLOBAL", "LOCAL", "TEMPORARY", "TEMP", "UNLOGGED", "OR", "REPLACE", "MEMORY", "CACHED");

    private static final Set<String> CONSTRAINT_KEYWORDS =
            Set.of("CONSTRAINT", "PRIMARY", "UNIQUE", "FOREIGN", "CHECK", "LIKE", "EXCLUDE");

    // Определения индексов внутри CREATE TABLE (MySQL): KEY idx_name (user_id)
    private static final Set<String> INDEX_KEYWORDS = Set.of("KEY", "INDEX", "FULLTEXT", "SPATIAL");

    private static final String ENUM_TYPE = "enum";
    private static final String SEQUENCE_FUNCTION = "nextval";

    @Override
    public ParsedSchema parse(String content) {
        SchemaBuilder builder = new SchemaBuilder();
        for (List<SqlToken> statement : SqlTokenizer.statements(content)) {
            SqlTokenReader reader = new SqlTokenReader(statement);
            if (reader.skipWord("CREATE")) {
                parseCreate(reader, builder);
            } else if (reader.skipWords("ALTER", "TABLE")) {
                parseAlterTable(reader, builder);
            }
        }
        return builder.build();
    }

    @Override
    public SchemaType getType() {
        return SchemaType.SQL;
    }

    private void parseCreate(SqlTokenReader reader, SchemaBuilder builder) {
        if (reader.skipWords("UNIQUE", "INDEX")) {
            parseUniqueIndex(reader, builder);
            return;
        }
        if (reader.skipWord("TYPE")) {
            parseEnumType(reader, builder);
            return;
        }

        while (isOneOf(reader.peek(), TABLE_MODIFIERS)) {
            reader.next();
        }
        if (reader.skipWord("TABLE")) {
            parseCreateTable(reader, builder);
        }
    }

    private void parseCreateTable(SqlTokenReader reader, SchemaBuilder builder) {
        reader.skipWords("IF", "NOT", "EXISTS");
        String tableName = reader.readName();
        if (tableName == null) {
            return;
        }

        // CREATE TABLE ... AS SELECT, PARTITION OF, LIKE: колонки берутся из другого места, прочитать их нельзя
        if (!reader.isGroupStart()) {
            builder.warn(
                    String.format("Table [%s]: columns are not listed in CREATE TABLE, table is skipped", tableName));
            return;
        }

        builder.createTable(tableName);
        for (List<SqlToken> element : SqlTokenReader.splitByComma(reader.readGroup())) {
            parseTableElement(tableName, element, builder);
        }
    }

    private void parseTableElement(String tableName, List<SqlToken> element, SchemaBuilder builder) {
        if (isIndexDefinition(element)) {
            return;
        }

        SqlTokenReader reader = new SqlTokenReader(element);
        if (isOneOf(reader.peek(), CONSTRAINT_KEYWORDS)) {
            if (reader.isWord("LIKE")) {
                builder.warn(String.format("Table [%s]: columns copied by LIKE are not read", tableName));
            }
            parseConstraint(tableName, reader, builder);
        } else {
            addColumn(tableName, reader, builder);
        }
    }

    private void addColumn(String tableName, SqlTokenReader reader, SchemaBuilder builder) {
        ColumnSchema column = parseColumn(reader);
        if (column != null) {
            builder.addColumn(tableName, column);
        }
    }

    /**
     * Определение колонки: имя, тип и ограничения в произвольном порядке
     */
    @Nullable
    private ColumnSchema parseColumn(SqlTokenReader reader) {
        String name = reader.readName();
        if (name == null) {
            return null;
        }

        ColumnType type = ColumnTypeReader.read(reader);
        ColumnSchema column = new ColumnSchema().setName(name).setType(type);
        // MySQL: значения перечисления записаны прямо в типе колонки - enum('NEW', 'DONE')
        if (ENUM_TYPE.equals(type.name())) {
            column.setAllowedValues(type.arguments());
        }

        while (reader.hasNext()) {
            if (reader.skipWords("NOT", "NULL")) {
                column.setNotNull(true);
            } else if (reader.skipWords("PRIMARY", "KEY")) {
                column.setNotNull(true).setUnique(true);
            } else if (reader.skipWord("UNIQUE")) {
                column.setUnique(true);
            } else if (reader.skipWord("REFERENCES")) {
                column.setReference(readReferenceTarget(reader));
            } else if (reader.skipWords("GENERATED", "ALWAYS", "AS", "IDENTITY")) {
                column.setDatabaseGenerated(true);
            } else if (reader.skipWord("AS")) {
                // Вычисляемая колонка: GENERATED ALWAYS AS (выражение) или AS (выражение)
                column.setDatabaseGenerated(column.isDatabaseGenerated() || reader.isGroupStart());
            } else if (reader.skipWord("AUTO_INCREMENT")
                    || reader.skipWord("AUTOINCREMENT")
                    || reader.skipWord("IDENTITY")) {
                column.setAutoIncrement(true);
            } else if (reader.skipWord("DEFAULT")) {
                // Значение по умолчанию из последовательности - то же, что serial
                column.setAutoIncrement(column.isAutoIncrement() || reader.isWord(SEQUENCE_FUNCTION));
            } else if (reader.isGroupStart()) {
                // Содержимое скобок (CHECK, выражение DEFAULT) к ограничениям самой колонки не относится
                reader.readGroup();
            } else {
                reader.next();
            }
        }
        return column;
    }

    /**
     * Ограничение таблицы: [CONSTRAINT имя] PRIMARY KEY (...) | UNIQUE (...) | FOREIGN KEY (...) REFERENCES ...
     */
    private void parseConstraint(String tableName, SqlTokenReader reader, SchemaBuilder builder) {
        if (reader.skipWord("CONSTRAINT")) {
            reader.readName();
        }

        if (reader.skipWords("PRIMARY", "KEY")) {
            builder.primaryKey(tableName, readColumnNames(reader));
        } else if (reader.skipWord("UNIQUE")) {
            builder.unique(tableName, readColumnNames(reader));
        } else if (reader.skipWords("FOREIGN", "KEY")) {
            List<String> columnNames = readColumnNames(reader);
            if (reader.skipWord("REFERENCES")) {
                builder.foreignKey(tableName, columnNames, readReferenceTarget(reader));
            }
        }
    }

    // ALTER TABLE [IF EXISTS] [ONLY] имя действие [, действие ...]
    private void parseAlterTable(SqlTokenReader reader, SchemaBuilder builder) {
        reader.skipWords("IF", "EXISTS");
        reader.skipWord("ONLY");
        String tableName = reader.readName();
        if (tableName == null || !builder.hasTable(tableName)) {
            return;
        }

        for (List<SqlToken> action : SqlTokenReader.splitByComma(reader.rest())) {
            parseAlterAction(tableName, action, builder);
        }
    }

    private void parseAlterAction(String tableName, List<SqlToken> action, SchemaBuilder builder) {
        SqlTokenReader reader = new SqlTokenReader(action);
        if (reader.skipWord("ADD")) {
            List<SqlToken> definition = action.subList(1, action.size());
            if (isOneOf(reader.peek(), CONSTRAINT_KEYWORDS)) {
                parseConstraint(tableName, reader, builder);
            } else if (!isIndexDefinition(definition)) {
                reader.skipWord("COLUMN");
                reader.skipWords("IF", "NOT", "EXISTS");
                addColumn(tableName, reader, builder);
            }
        } else if (reader.skipWords("DROP", "COLUMN")) {
            reader.skipWords("IF", "EXISTS");
            builder.dropColumn(tableName, reader.readName());
        } else if (reader.skipWord("ALTER")) {
            reader.skipWord("COLUMN");
            String columnName = reader.readName();
            if (reader.skipWords("SET", "NOT", "NULL")) {
                builder.notNull(tableName, columnName, true);
            } else if (reader.skipWords("DROP", "NOT", "NULL")) {
                builder.notNull(tableName, columnName, false);
            }
        }
    }

    // CREATE UNIQUE INDEX [CONCURRENTLY] [IF NOT EXISTS] [имя] ON [ONLY] таблица [USING метод] (колонки) [WHERE ...]
    private void parseUniqueIndex(SqlTokenReader reader, SchemaBuilder builder) {
        while (reader.hasNext() && !reader.isWord("ON")) {
            reader.next();
        }
        reader.skipWord("ON");
        reader.skipWord("ONLY");

        String tableName = reader.readName();
        if (reader.skipWord("USING")) {
            reader.readName();
        }
        if (tableName == null || !reader.isGroupStart()) {
            return;
        }

        List<String> columnNames = readColumnNames(reader);
        // Частичный индекс не делает уникальными все значения колонки
        if (!reader.isWord("WHERE")) {
            builder.unique(tableName, columnNames);
        }
    }

    // CREATE TYPE имя AS ENUM ('a', 'b')
    private void parseEnumType(SqlTokenReader reader, SchemaBuilder builder) {
        String typeName = reader.readName();
        if (typeName == null || !reader.skipWords("AS", "ENUM") || !reader.isGroupStart()) {
            return;
        }

        List<String> values = reader.readGroup().stream()
                .filter(token -> token.kind() == SqlToken.Kind.STRING)
                .map(SqlToken::text)
                .toList();
        builder.declareEnum(typeName, values);
    }

    /**
     * Читает список колонок ключа или индекса из ближайших скобок: (a, b DESC). Выражения в индексе вместо
     * имён колонок дают имена, которых в таблице нет, и такой ключ не применяется
     */
    private List<String> readColumnNames(SqlTokenReader reader) {
        while (reader.hasNext() && !reader.isGroupStart()) {
            reader.next();
        }

        return SqlTokenReader.splitByComma(reader.readGroup()).stream()
                .map(part -> new SqlTokenReader(part).readName())
                .filter(Objects::nonNull)
                .toList();
    }

    // REFERENCES таблица [(колонки)]
    @Nullable
    private String readReferenceTarget(SqlTokenReader reader) {
        String target = reader.readName();
        if (target == null || !reader.isGroupStart()) {
            return target;
        }

        List<String> columnNames = readColumnNames(reader);
        return target + "(" + String.join(", ", columnNames) + ")";
    }

    /**
     * Отличает определение индекса от колонки с именем key или index: у индекса в скобках перечислены
     * колонки (KEY idx (user_id)), у колонки - числовые аргументы типа (key varchar(255)) или скобок нет вовсе
     */
    private boolean isIndexDefinition(List<SqlToken> element) {
        if (element.isEmpty() || !isOneOf(element.get(0), INDEX_KEYWORDS)) {
            return false;
        }

        SqlTokenReader reader = new SqlTokenReader(element);
        while (reader.hasNext() && !reader.isGroupStart()) {
            reader.next();
        }
        return reader.readGroup().stream()
                .anyMatch(token ->
                        token.isName() && !Character.isDigit(token.text().charAt(0)));
    }

    private boolean isOneOf(@Nullable SqlToken token, Set<String> keywords) {
        return token != null
                && token.kind() == SqlToken.Kind.WORD
                && keywords.contains(token.text().toUpperCase(Locale.ROOT));
    }
}
