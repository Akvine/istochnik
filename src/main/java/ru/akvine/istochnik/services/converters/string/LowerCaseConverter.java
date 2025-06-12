package ru.akvine.istochnik.services.converters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;

@Service
public class LowerCaseConverter extends StringConverter<String, String> {
    @Override
    public List<String> convert(List<String> input, String[] arguments) {
        return input.stream().map(String::toLowerCase).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.LOWER_CASE;
    }
}
