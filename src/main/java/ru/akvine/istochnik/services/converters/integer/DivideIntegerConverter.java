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

    @Override
    public void validateArgument(Double[] arguments) {
        if (arguments == null || arguments.length == 0 || arguments[0] == null) {
            throw new IllegalArgumentException("arguments can't be null or empty!");
        }

        if (arguments[0] == 0) {
            throw new IllegalArgumentException("Division by zero");
        }
    }
}
