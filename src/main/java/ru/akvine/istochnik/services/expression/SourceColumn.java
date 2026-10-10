package ru.akvine.istochnik.services.expression;

import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.services.dto.GenerateColumn;

/**
 * Колонка, от которой зависит вычисляемая: её описание и уже готовые значения
 */
public record SourceColumn(GenerateColumn column, List<?> values) {

    /**
     * Значение колонки в том виде, в котором оно доступно выражению
     */
    @Nullable
    public Object valueAt(int rowIndex) {
        Object value = values.get(rowIndex);

        // Целые числа приводятся к long, дробные - к double: в выражении у числа из любой колонки один и тот же тип
        if (value instanceof Integer || value instanceof Short || value instanceof Byte) {
            return ((Number) value).longValue();
        }
        if (value instanceof Float) {
            return ((Number) value).doubleValue();
        }

        // Значения decimal хранятся строками, но в выражении участвуют как точные числа.
        // Если конвертеры колонки превратили значение в нечисловую строку, оно остаётся строкой
        if (value instanceof String text && column.getCustomType() == CustomType.DECIMAL) {
            BigDecimal decimal = ExpressionFunctions.parseDecimal(text);
            return decimal == null ? value : decimal;
        }

        return value;
    }
}
