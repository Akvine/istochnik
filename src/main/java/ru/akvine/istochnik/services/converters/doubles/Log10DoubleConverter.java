package ru.akvine.istochnik.services.converters.doubles;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;

@Service
public class Log10DoubleConverter extends DoubleConverter<Double, Double> {
    @Override
    public List<Double> convert(List<Double> input, Double[] arguments) {
        return input.stream().map(Math::log10).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.LOG10;
    }
}
