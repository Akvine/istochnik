package ru.akvine.istochnik.services.converters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;

@Service
public class UpperCaseConverter extends StringConverter<String, String> {
    @Override
    public List<String> convert(List<String> input, String[] arguments) {
        return input.stream().map(String::toUpperCase).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.UPPER_CASE;
    }
}
