package ru.akvine.istochnik.services.converters.doubles;

import java.util.List;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

@Service
public class SinDoubleConverter extends DoubleConverter<Double, Double> {
    @Override
    public List<Double> convert(
            List<Double> input, Double[] arguments, RandomGenerator randomGenerator, double probability) {
        return input.stream()
                .map(value -> randomGenerator.nextDouble() < probability ? Math.sin(value) : value)
                .toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.SIN;
    }
}
