package ru.akvine.istochnik.services.converters.integer;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;

@Service
public class PowIntegerConverter extends IntegerConverter<Integer, Double> {
    @Override
    public List<Integer> convert(List<Integer> input, Double[] argument) {
        return input.stream().map(value -> (int) Math.pow(value, argument[0])).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.POW;
    }
}
