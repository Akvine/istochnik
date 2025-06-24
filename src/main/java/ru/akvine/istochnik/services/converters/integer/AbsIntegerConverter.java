package ru.akvine.istochnik.services.converters.integer;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;
import java.util.random.RandomGenerator;

@Service
public class AbsIntegerConverter extends IntegerConverter<Long, Double> {
    @Override
    public List<Long> convert(List<Long> input, Double[] argument, RandomGenerator randomGenerator) {
        return input.stream().map(Math::abs).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.ABS;
    }
}
