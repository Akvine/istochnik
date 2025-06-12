package ru.akvine.istochnik.services.converters.string;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;

@Service
public class ReverseConverter extends StringConverter<String, String> {
    @Override
    public List<String> convert(List<String> input, String[] arguments) {
        return input.stream().map(StringUtils::reverse).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.REVERSE;
    }
}
