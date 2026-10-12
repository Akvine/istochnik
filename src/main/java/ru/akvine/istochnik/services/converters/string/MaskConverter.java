package ru.akvine.istochnik.services.converters.string;

import java.util.List;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

/**
 * Маскирует середину строки: первые и последние символы остаются, остальные заменяются символом маски.
 * Аргументы: сколько символов оставить в начале (по умолчанию 0), сколько в конце (по умолчанию 0)
 * и символ маски (по умолчанию звёздочка)
 */
@Service
public class MaskConverter extends StringConverter<String, String> {
    private static final char DEFAULT_MASK_CHAR = '*';

    @Override
    public List<String> convert(
            List<String> input, String[] arguments, RandomGenerator randomGenerator, double probability) {
        int visibleStart = visibleStart(arguments);
        int visibleEnd = visibleEnd(arguments);
        char maskChar = maskChar(arguments);

        return input.stream()
                .map(value -> {
                    if (value == null) {
                        return null;
                    }

                    return randomGenerator.nextDouble() < probability
                            ? mask(value, visibleStart, visibleEnd, maskChar)
                            : value;
                })
                .toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.MASK;
    }

    @Override
    public void validateArgument(String[] arguments) {
        visibleStart(arguments);
        visibleEnd(arguments);
        maskChar(arguments);
    }

    // Если строка не длиннее видимой части, маскировать в ней нечего
    private String mask(String value, int visibleStart, int visibleEnd, char maskChar) {
        // Сумма считается в long: оба числа могут быть близки к максимальному int
        long maskedLength = value.length() - ((long) visibleStart + visibleEnd);
        if (maskedLength <= 0) {
            return value;
        }

        return value.substring(0, visibleStart)
                + String.valueOf(maskChar).repeat((int) maskedLength)
                + value.substring(value.length() - visibleEnd);
    }

    private int visibleStart(String[] arguments) {
        return StringConverterArguments.optionalInt(arguments, 0, "visibleStart", Integer.MAX_VALUE, 0);
    }

    private int visibleEnd(String[] arguments) {
        return StringConverterArguments.optionalInt(arguments, 1, "visibleEnd", Integer.MAX_VALUE, 0);
    }

    private char maskChar(String[] arguments) {
        return StringConverterArguments.optionalChar(arguments, 2, "maskChar", DEFAULT_MASK_CHAR);
    }
}
