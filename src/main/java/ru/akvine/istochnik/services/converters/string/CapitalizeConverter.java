package ru.akvine.istochnik.services.converters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;
import java.util.random.RandomGenerator;

@Service
public class CapitalizeConverter extends StringConverter<String, String> {

    @Override
    public List<String> convert(List<String> input, String[] arguments, RandomGenerator randomGenerator) {
        return input.stream().map(value -> {
            String firstCharacter = value.substring(0, 1).toUpperCase();
            String otherCharacters = value.substring(1);
            return firstCharacter + otherCharacters;
        }).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.CAPITALIZE;
    }
}
