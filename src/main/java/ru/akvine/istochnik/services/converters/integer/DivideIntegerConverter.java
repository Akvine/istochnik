package ru.akvine.istochnik.services.converters.integer;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;

@Service
public class DivideIntegerConverter extends IntegerConverter<Integer, Double> {
    @Override
    public List<Integer> convert(List<Integer> input, Double[] arguments) {
        return input.stream().map(value -> (int) (value / arguments[0])).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.DIVIDE;
    }
}
