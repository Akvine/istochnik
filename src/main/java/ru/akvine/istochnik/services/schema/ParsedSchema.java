package ru.akvine.istochnik.services.schema;

import java.util.List;

/**
 * Результат разбора схемы
 *
 * @param tables   таблицы в порядке их создания в схеме
 * @param warnings что в схеме не удалось учесть
 */
public record ParsedSchema(List<TableSchema> tables, List<String> warnings) {}
