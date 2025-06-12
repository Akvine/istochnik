package ru.akvine.istochnik.services.converters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;

@Service
public class AddAfterConverter extends StringConverter<String, String> {
    @Override
    public List<String> convert(List<String> input, String[] arguments) {
        String arg = arguments[0];
        return input.stream().map(value -> value + arg).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.ADD_AFTER;
    }
}
