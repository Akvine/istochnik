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
}
