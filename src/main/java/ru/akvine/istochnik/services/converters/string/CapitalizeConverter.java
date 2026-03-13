package ru.akvine.istochnik.services.converters.string;

import java.util.List;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

@Service
public class CapitalizeConverter extends StringConverter<String, String> {

    @Override
    public List<String> convert(
            List<String> input, String[] arguments, RandomGenerator randomGenerator, double probability) {
        return input.stream()
                .map(value -> {
                    if (randomGenerator.nextDouble() < probability) {
                        String firstCharacter = value.substring(0, 1).toUpperCase();
                        String otherCharacters = value.substring(1);
                        return firstCharacter + otherCharacters;
                    } else {
                        return value;
                    }
                })
                .toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.CAPITALIZE;
    }
}
