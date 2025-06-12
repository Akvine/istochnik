package ru.akvine.istochnik.services.converters.doubles;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;

@Service
public class PlusDoubleConverter extends DoubleConverter<Double, Double> {
    @Override
    public List<Double> convert(List<Double> input, Double[] arguments) {
        return input.stream().map(value -> value + arguments[0]).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.PLUS;
    }
}
