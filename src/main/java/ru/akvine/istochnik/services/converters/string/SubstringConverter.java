package ru.akvine.istochnik.services.converters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;

@Service
public class SubstringConverter extends StringConverter<String, String> {
    @Override
    public List<String> convert(List<String> input, String[] arguments) {
        int beginIndex = Integer.parseInt(arguments[0]);
        int endIndex = Integer.parseInt(arguments[1]);

        return input.stream().map(value -> value.substring(beginIndex, endIndex)).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.SUBSTRING;
    }
}
