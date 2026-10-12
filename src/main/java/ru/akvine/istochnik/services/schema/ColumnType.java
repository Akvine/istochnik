package ru.akvine.istochnik.services.schema;

import jakarta.annotation.Nullable;
import java.util.List;

/**
 * Тип колонки в том виде, в каком он записан в схеме
 *
 * @param name      имя типа в нижнем регистре, слова разделены пробелом: timestamp with time zone
 * @param arguments аргументы типа: длина строки, точность и масштаб числа, значения перечисления
 * @param array     является ли колонка массивом
 */
public record ColumnType(String name, List<String> arguments, boolean array) {

    /**
     * @return целочисленный аргумент типа или null, если аргумента нет или он не является числом
     */
    @Nullable
    public Integer intArgument(int index) {
        if (index >= arguments.size()) {
            return null;
        }

        // Число может сопровождаться единицей измерения: varchar2(100 char)
        String argument = arguments.get(index).trim();
        int digitsCount = 0;
        while (digitsCount < argument.length() && Character.isDigit(argument.charAt(digitsCount))) {
            digitsCount++;
        }

        try {
            return digitsCount == 0 ? null : Integer.valueOf(argument.substring(0, digitsCount));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    @Override
    public String toString() {
        String suffix = arguments.isEmpty() ? "" : "(" + String.join(", ", arguments) + ")";
        return name + suffix + (array ? "[]" : "");
    }
}
