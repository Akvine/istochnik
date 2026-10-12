package ru.akvine.istochnik.services.schema;

import jakarta.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Таблица, прочитанная из схемы. Имена колонок сравниваются без учёта регистра
 */
@RequiredArgsConstructor
public class TableSchema {
    @Getter
    private final String name;

    // Схема приходит от клиента и может содержать тысячи колонок: поиск по имени не должен зависеть от их числа
    private final Map<String, ColumnSchema> columnsByName = new LinkedHashMap<>();

    /**
     * @return колонки в порядке их добавления
     */
    public Collection<ColumnSchema> getColumns() {
        return columnsByName.values();
    }

    @Nullable
    public ColumnSchema findColumn(@Nullable String columnName) {
        return columnName == null ? null : columnsByName.get(key(columnName));
    }

    /**
     * Колонка с уже существующим именем не добавляется
     */
    public void addColumn(ColumnSchema column) {
        columnsByName.putIfAbsent(key(column.getName()), column);
    }

    public void removeColumn(@Nullable String columnName) {
        if (columnName != null) {
            columnsByName.remove(key(columnName));
        }
    }

    private String key(String columnName) {
        return columnName.toLowerCase(Locale.ROOT);
    }
}
