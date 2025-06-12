package ru.akvine.istochnik.services.converters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;

@Service
public class ReplaceAllConverter extends StringConverter<String, String> {
    @Override
    public List<String> convert(List<String> input, String[] arguments) {
        return input.stream().map(value -> value.replaceAll(arguments[0], arguments[1])).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.REPLACE_ALL;
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
