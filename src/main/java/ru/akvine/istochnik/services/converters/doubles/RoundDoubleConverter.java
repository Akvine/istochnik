package ru.akvine.istochnik.services.converters.doubles;

import java.util.List;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.compozit.commons.utils.MathUtils;
import ru.akvine.istochnik.enums.ConverterType;

@Service
public class RoundDoubleConverter extends DoubleConverter<Double, Double> {
    @Override
    public List<Double> convert(
            List<Double> input, Double[] arguments, RandomGenerator randomGenerator, double probability) {
        int accuracy = arguments[0].intValue();
        return input.stream()
                .map(value -> randomGenerator.nextDouble() < probability ? MathUtils.round(value, accuracy) : value)
                .toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.ROUND;
    }

    @Override
    public void validateArgument(Double[] arguments) {
        if (arguments == null || arguments.length == 0 || arguments[0] == null) {
            throw new IllegalArgumentException("arguments can't be null or empty!");
        }
    }
}
