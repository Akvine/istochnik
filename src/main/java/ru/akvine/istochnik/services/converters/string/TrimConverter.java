package ru.akvine.istochnik.services.converters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;
import java.util.random.RandomGenerator;

@Service
public class TrimConverter extends StringConverter<String, String> {
    @Override
    public List<String> convert(List<String> input, String[] arguments, RandomGenerator randomGenerator) {
        return input.stream().map(String::trim).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.TRIM;
    }
}
