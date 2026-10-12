package ru.akvine.istochnik.services.converters.string;

import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Дополняет строку до заданной длины одним и тем же символом. Строки не короче этой длины не меняются.
 * Аргументы: длина и символ дополнения (по умолчанию пробел)
 */
abstract class AbstractPadConverter extends StringConverter<String, String> {
    // Длина ограничена: дополнение применяется к каждой строке колонки, и большая длина исчерпала бы память
    static final int MAX_LENGTH = 1000;

    private static final char DEFAULT_PAD_CHAR = ' ';

    @Override
    public List<String> convert(
            List<String> input, String[] arguments, RandomGenerator randomGenerator, double probability) {
        int length = length(arguments);
        char padChar = padChar(arguments);

        return input.stream()
                .map(value -> {
                    if (value == null) {
                        return null;
                    }

                    return randomGenerator.nextDouble() < probability ? pad(value, length, padChar) : value;
                })
                .toList();
    }

    @Override
    public void validateArgument(String[] arguments) {
        length(arguments);
        padChar(arguments);
    }

    protected abstract String pad(String value, int length, char padChar);

    private int length(String[] arguments) {
        return StringConverterArguments.requiredInt(arguments, 0, "length", MAX_LENGTH);
    }

    private char padChar(String[] arguments) {
        return StringConverterArguments.optionalChar(arguments, 1, "padChar", DEFAULT_PAD_CHAR);
    }
}
