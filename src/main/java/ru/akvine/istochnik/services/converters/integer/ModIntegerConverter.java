package ru.akvine.istochnik.services.converters.integer;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;

@Service
public class ModIntegerConverter extends IntegerConverter<Integer, Double> {
    @Override
    public List<Integer> convert(List<Integer> input, Double[] arguments) {
        Double operand = arguments[0];
        return input.stream().map(value -> (int) (value % operand)).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.MOD;
    }
}
