package ru.akvine.istochnik.services.converters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;

@Service
public class AddBeforeConverter extends StringConverter<String, String> {
    @Override
    public List<String> convert(List<String> input, String[] arguments) {
        String arg = arguments[0];
        return input.stream().map(value -> arg + value).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.ADD_BEFORE;
    }

    @Override
    public void validateArgument(String[] arguments) {
        if (arguments == null || arguments.length == 0 || arguments[0] == null) {
            throw new IllegalArgumentException("arguments can't be null or empty!");
        }
    }
}
