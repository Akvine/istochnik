package ru.akvine.istochnik.services.converters.integer;

import java.util.List;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

@Service
public class MultiplyIntegerConverter extends IntegerConverter<Long, Double> {
    @Override
    public List<Long> convert(
            List<Long> input, Double[] arguments, RandomGenerator randomGenerator, double probability) {
        return input.stream()
                .map(value -> {
                    if (value == null) {
                        return null;
                    } else {
                        if (randomGenerator.nextDouble() < probability) {
                            return (long) (value * arguments[0]);
                        } else {
                            return value;
                        }
                    }
                })
                .toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.MULTIPLY;
    }

    @Override
    public void validateArgument(Double[] arguments) {
        if (arguments == null || arguments.length == 0 || arguments[0] == null) {
            throw new IllegalArgumentException("arguments can't be null or empty!");
        }
    }
}
