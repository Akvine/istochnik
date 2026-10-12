package ru.akvine.istochnik.services.schema;

import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Собирает описание таблиц по мере чтения схемы. Парсеры разных форматов сообщают о создании таблиц
 * и о последующих изменениях одними и теми же операциями, поэтому правила их применения записаны один раз.
 * <p>
 * Изменения таблиц, которых в схеме нет, пропускаются: схема может быть одной миграцией из многих.
 */
public class SchemaBuilder {
    private static final char NAME_SEPARATOR = '.';

    // Таблицы по полному имени в нижнем регистре, в порядке создания
    private final Map<String, TableSchema> tables = new LinkedHashMap<>();
    // Полные имена таблиц по имени без схемы БД
    private final Map<String, Set<String>> tableKeysBySimpleName = new HashMap<>();
    private final Map<String, List<String>> enumTypes = new HashMap<>();
    private final List<String> warnings = new ArrayList<>();

    /**
     * Повторное создание таблицы заменяет её прежнее описание
     */
    public void createTable(String name) {
        String key = key(name);
        tables.remove(key);
        tables.put(key, new TableSchema(name));
        tableKeysBySimpleName
                .computeIfAbsent(simpleName(name), simpleName -> new LinkedHashSet<>())
                .add(key);
    }

    public boolean hasTable(String name) {
        return findTable(name) != null;
    }

    /**
     * Колонка с уже существующим именем не добавляется
     */
    public void addColumn(String tableName, ColumnSchema column) {
        TableSchema table = findTable(tableName);
        if (table == null) {
            return;
        }

        List<String> enumValues = enumTypes.get(simpleName(column.getType().name()));
        if (enumValues != null && column.getAllowedValues().isEmpty()) {
            column.setAllowedValues(enumValues);
        }
        table.addColumn(column);
    }

    public void dropColumn(String tableName, String columnName) {
        TableSchema table = findTable(tableName);
        if (table != null) {
            table.removeColumn(columnName);
        }
    }

    public void primaryKey(String tableName, List<String> columnNames) {
        List<ColumnSchema> columns = findColumns(tableName, columnNames);
        columns.forEach(column -> column.setNotNull(true));
        applyUniqueness(tableName, columns);
    }

    public void unique(String tableName, List<String> columnNames) {
        applyUniqueness(tableName, findColumns(tableName, columnNames));
    }

    /**
     * @param reference таблица и колонки, на которые ссылается ключ, например roles(id)
     */
    public void foreignKey(String tableName, List<String> columnNames, String reference) {
        findColumns(tableName, columnNames).forEach(column -> column.setReference(reference));
    }

    public void notNull(String tableName, String columnName, boolean notNull) {
        TableSchema table = findTable(tableName);
        ColumnSchema column = table == null ? null : table.findColumn(columnName);
        if (column != null) {
            column.setNotNull(notNull);
        }
    }

    /**
     * Объявляет тип-перечисление: колонки этого типа, добавленные позже, получат его значения как допустимые
     */
    public void declareEnum(String typeName, List<String> values) {
        if (!values.isEmpty()) {
            enumTypes.put(simpleName(typeName), List.copyOf(values));
        }
    }

    public void warn(String message) {
        warnings.add(message);
    }

    public ParsedSchema build() {
        return new ParsedSchema(List.copyOf(tables.values()), List.copyOf(warnings));
    }

    // Генератор обеспечивает уникальность только в пределах одной колонки. Набор колонок уникален, если уникальна
    // хотя бы одна из них, поэтому для составного ключа уникальной делается его первая колонка
    private void applyUniqueness(String tableName, List<ColumnSchema> columns) {
        if (columns.isEmpty()) {
            return;
        }

        boolean alreadyUnique = columns.stream().anyMatch(column -> column.isUnique() || column.isAutoIncrement());
        if (alreadyUnique) {
            return;
        }

        ColumnSchema first = columns.get(0);
        first.setUnique(true);
        if (columns.size() > 1) {
            List<String> names = columns.stream().map(ColumnSchema::getName).toList();
            warn(String.format(
                    "Table [%s]: uniqueness of composite key %s is provided by column [%s] only",
                    tableName, names, first.getName()));
        }
    }

    /**
     * @return колонки таблицы в порядке имён или пустой список, если нет таблицы или хотя бы одной из колонок
     */
    private List<ColumnSchema> findColumns(String tableName, List<String> columnNames) {
        TableSchema table = findTable(tableName);
        if (table == null) {
            return List.of();
        }

        List<ColumnSchema> columns = columnNames.stream()
                .map(table::findColumn)
                .filter(Objects::nonNull)
                .toList();
        return columns.size() == columnNames.size() ? columns : List.of();
    }

    // Одну и ту же таблицу в схеме могут называть и с именем схемы БД, и без него:
    // CREATE TABLE users и ALTER TABLE public.users
    @Nullable
    private TableSchema findTable(@Nullable String name) {
        if (name == null) {
            return null;
        }

        TableSchema table = tables.get(key(name));
        if (table != null) {
            return table;
        }

        Set<String> sameSimpleName = tableKeysBySimpleName.getOrDefault(simpleName(name), Set.of());
        return sameSimpleName.size() == 1 ? tables.get(sameSimpleName.iterator().next()) : null;
    }

    private String key(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    private String simpleName(String name) {
        return key(name.substring(name.lastIndexOf(NAME_SEPARATOR) + 1));
    }
}
