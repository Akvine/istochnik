package ru.akvine.istochnik.services.converters.doubles;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;

@Service
public class DivideDoubleConverter extends DoubleConverter<Double, Double> {
    @Override
    public List<Double> convert(List<Double> input, Double[] arguments) {
        return input.stream().map(value -> value / arguments[0]).toList();
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
