package ru.akvine.istochnik.services.converters.doubles;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;
import java.util.random.RandomGenerator;

@Service
public class ExpDoubleConverter extends DoubleConverter<Double, Double> {
    @Override
    public List<Double> convert(List<Double> input,
                                Double[] arguments,
                                RandomGenerator randomGenerator,
                                double probability) {
        return input.stream().map(value ->
                        randomGenerator.nextDouble() < probability ? Math.exp(value) : value)
                .toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.EXP;
    }
}
