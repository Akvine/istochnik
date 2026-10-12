package ru.akvine.istochnik.services.converters.string;

import java.util.List;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

/**
 * Обрезает строку до заданной длины. Строки короче этой длины не меняются - в отличие от конвертера substring,
 * для которого выход индекса за пределы строки является ошибкой.
 * Аргумент: наибольшая длина строки
 */
@Service
public class TruncateConverter extends StringConverter<String, String> {
    @Override
    public List<String> convert(
            List<String> input, String[] arguments, RandomGenerator randomGenerator, double probability) {
        int maxLength = maxLength(arguments);

        return input.stream()
                .map(value -> {
                    if (value == null) {
                        return null;
                    }

                    boolean truncate = randomGenerator.nextDouble() < probability && value.length() > maxLength;
                    return truncate ? value.substring(0, maxLength) : value;
                })
                .toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.TRUNCATE;
    }

    @Override
    public void validateArgument(String[] arguments) {
        maxLength(arguments);
    }

    private int maxLength(String[] arguments) {
        return StringConverterArguments.requiredInt(arguments, 0, "maxLength", Integer.MAX_VALUE);
    }
}
