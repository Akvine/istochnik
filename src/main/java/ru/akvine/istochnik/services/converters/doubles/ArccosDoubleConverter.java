package ru.akvine.istochnik.services.converters.doubles;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;

@Service
public class ArccosDoubleConverter extends DoubleConverter<Double, Double> {
    @Override
    public List<Double> convert(List<Double> input, Double[] arguments) {
        return input.stream().map(Math::acos).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.ARCCOS;
    }
}
