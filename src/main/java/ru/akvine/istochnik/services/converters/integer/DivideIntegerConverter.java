package ru.akvine.istochnik.services.converters.integer;

import java.util.List;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

@Service
public class DivideIntegerConverter extends IntegerConverter<Long, Double> {
    @Override
    public List<Long> convert(
            List<Long> input, Double[] arguments, RandomGenerator randomGenerator, double probability) {
        return input.stream()
                .map(value -> randomGenerator.nextDouble() < probability ? (long) (value / arguments[0]) : value)
                .toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.DIVIDE;
    }

    @Override
    public void validateArgument(Double[] arguments) {
        if (arguments == null || arguments.length == 0 || arguments[0] == null) {
            throw new IllegalArgumentException("arguments can't be null or empty!");
        }

        if (arguments[0] == 0) {
            throw new IllegalArgumentException("Division by zero");
        }
    }
}
