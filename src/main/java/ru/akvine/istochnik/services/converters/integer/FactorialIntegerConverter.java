package ru.akvine.istochnik.services.converters.integer;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;
import java.util.random.RandomGenerator;

@Service
public class FactorialIntegerConverter extends IntegerConverter<Long, Double> {
    @Override
    public List<Long> convert(List<Long> input, Double[] arguments, RandomGenerator randomGenerator) {
        return input.stream().map(value -> {
            long result = 1;
            for (long i = 2; i <= value; i++) {
                result *= i;
            }
            return result;
        }).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.FACTORIAL;
    }
}
