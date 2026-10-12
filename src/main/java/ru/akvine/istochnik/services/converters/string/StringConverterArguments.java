package ru.akvine.istochnik.services.converters.string;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * Разбор аргументов строковых конвертеров. Аргументы приходят строками: числа из запроса приводятся к строке.
 * Методы бросают {@link IllegalArgumentException} с описанием того, что не так с аргументом
 */
@UtilityClass
class StringConverterArguments {

    /**
     * @return целое число от 0 до max из обязательного аргумента с номером index
     */
    int requiredInt(String[] arguments, int index, String name, int max) {
        if (isAbsent(arguments, index)) {
            throw new IllegalArgumentException("argument [" + name + "] is required");
        }
        return parseInt(arguments[index], name, max);
    }

    /**
     * @return целое число от 0 до max из аргумента с номером index или defaultValue, если аргумент не передан
     */
    int optionalInt(String[] arguments, int index, String name, int max, int defaultValue) {
        return isAbsent(arguments, index) ? defaultValue : parseInt(arguments[index], name, max);
    }

    /**
     * @return символ из аргумента с номером index или defaultValue, если аргумент не передан
     */
    char optionalChar(String[] arguments, int index, String name, char defaultValue) {
        // Пробел - допустимый символ, поэтому аргумент не проверяется на blank
        if (arguments == null || index >= arguments.length || StringUtils.isEmpty(arguments[index])) {
            return defaultValue;
        }

        String value = arguments[index];
        if (value.length() != 1) {
            throw new IllegalArgumentException("argument [" + name + "] must be a single character");
        }
        return value.charAt(0);
    }

    private boolean isAbsent(String[] arguments, int index) {
        return arguments == null || index >= arguments.length || StringUtils.isBlank(arguments[index]);
    }

    private int parseInt(String argument, String name, int max) {
        try {
            int value = Integer.parseInt(argument.trim());
            if (value >= 0 && value <= max) {
                return value;
            }
        } catch (NumberFormatException exception) {
            // Не число - та же ошибка, что и число вне диапазона
        }

        String range = max == Integer.MAX_VALUE ? "a non-negative integer" : "an integer between 0 and " + max;
        throw new IllegalArgumentException("argument [" + name + "] must be " + range);
    }
}
