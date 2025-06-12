package ru.akvine.istochnik.services.converters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.Base64;
import java.util.List;

@Service
public class Base64Converter extends StringConverter<String, String> {
    @Override
    public List<String> convert(List<String> input, String[] arguments) {
        return input.stream()
                .map(value -> new String(Base64.getEncoder().encode(value.getBytes())))
                .toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.BASE64;
    }
}
