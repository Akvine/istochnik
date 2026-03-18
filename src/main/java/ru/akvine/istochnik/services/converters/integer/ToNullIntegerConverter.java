package ru.akvine.istochnik.services.converters.integer;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;
import java.util.random.RandomGenerator;

@Service
public class ToNullIntegerConverter extends IntegerConverter<Long, Double> {
    @Override
    public List<Long> convert(List<Long> input, Double[] arguments, RandomGenerator randomGenerator, double probability) {
        return input.stream()
                .map(value -> {
                    if (value != null && randomGenerator.nextDouble() < probability) {
                        return null;
                    }

                    return value;
                })
                .toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.TO_NULL;
    }
}
