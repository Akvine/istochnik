package ru.akvine.istochnik.services.converters.doubles;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;

@Service
public class FloorDoubleConverter extends DoubleConverter<Double, Double> {
    @Override
    public List<Double> convert(List<Double> input, Double[] arguments) {
        return input.stream().map(Math::floor).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.FLOOR;
    }
}
