package ru.akvine.istochnik.services.converters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;
import java.util.random.RandomGenerator;

@Service
public class SubstringConverter extends StringConverter<String, String> {
    @Override
    public List<String> convert(List<String> input, String[] arguments, RandomGenerator randomGenerator) {
        int beginIndex = Integer.parseInt(arguments[0]);
        int endIndex = Integer.parseInt(arguments[1]);

        return input.stream().map(value -> value.substring(beginIndex, endIndex)).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.SUBSTRING;
    }

    @Override
    public void validateArgument(String[] arguments) {
        if (arguments == null || arguments.length == 0) {
            throw new IllegalArgumentException("arguments can't be null or empty!");
        }

        if (arguments.length < 2) {
            throw new IllegalArgumentException("arguments count can't be less than 2");
        }

        if (arguments[0] == null || arguments[1] == null) {
            throw new IllegalArgumentException("arguments can't be null or empty!");
        }
    }
}
