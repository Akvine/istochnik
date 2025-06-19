package ru.akvine.istochnik.services.converters.doubles;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;
import java.util.random.RandomGenerator;

@Service
public class ArcsinDoubleConverter extends DoubleConverter<Double, Double> {
    @Override
    public List<Double> convert(List<Double> input, Double[] arguments, RandomGenerator randomGenerator) {
        return input.stream().map(Math::asin).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.ARCSIN;
    }
}
