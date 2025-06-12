package ru.akvine.istochnik.services.converters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@Service
public class RandomNumericReplaceConverter extends StringConverter<String, String> {
    private final static char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private final Random random = new SecureRandom();

    @Override
    public List<String> convert(List<String> input, String[] arguments) {
        return input.stream().map(value -> {
            StringBuilder sb = new StringBuilder();
            char[] chars = value.toCharArray();
            for (char charValue : chars) {
                if (Character.isDigit(charValue)) {
                    int randomIndex = random.nextInt(DIGITS.length);
                    sb.append(DIGITS[randomIndex]);
                } else {
                    sb.append(charValue);
                }
            }

            return sb.toString();
        }).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.RANDOM_NUMERIC_REPLACE;
    }
}
