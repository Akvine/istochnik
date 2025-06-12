package ru.akvine.istochnik.services.converters.integer;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;

@Service
public class NegativeIntegerConverter extends IntegerConverter<Integer, Double> {
    @Override
    public List<Integer> convert(List<Integer> input, Double[] arguments) {
        return input.stream().map(value -> value * (-1)).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.NEGATIVE;
    }
}
