package ru.akvine.istochnik.services.converters.string;

import java.util.List;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

@Service
public class LowerCaseConverter extends StringConverter<String, String> {
    @Override
    public List<String> convert(
            List<String> input, String[] arguments, RandomGenerator randomGenerator, double probability) {
        return input.stream()
                .map(value -> randomGenerator.nextDouble() < probability ? value.toLowerCase() : value)
                .toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.LOWER_CASE;
    }
}
