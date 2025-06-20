package ru.akvine.istochnik.services.converters.string;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;
import java.util.random.RandomGenerator;

@Service
@RequiredArgsConstructor
public class RandomNumericReplaceConverter extends StringConverter<String, String> {
    private final static char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    @Override
    public List<String> convert(List<String> input, String[] arguments, RandomGenerator randomGenerator) {
        return input.stream().map(value -> {
            StringBuilder sb = new StringBuilder();
            char[] chars = value.toCharArray();
            for (char charValue : chars) {
                if (Character.isDigit(charValue)) {
                    int randomIndex = randomGenerator.nextInt(DIGITS.length);
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
