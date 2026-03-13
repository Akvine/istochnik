package ru.akvine.istochnik.services.converters.string;

import java.util.Base64;
import java.util.List;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

@Service
public class Base64Converter extends StringConverter<String, String> {
    @Override
    public List<String> convert(
            List<String> input, String[] arguments, RandomGenerator randomGenerator, double probability) {
        return input.stream()
                .map(value -> randomGenerator.nextDouble() < probability
                        ? new String(Base64.getEncoder().encode(value.getBytes()))
                        : value)
                .toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.BASE64;
    }
}
