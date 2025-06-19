package ru.akvine.istochnik.services.converters.doubles;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;

@Service
public class ShuffleDoubleConverter extends DoubleConverter<Double, Double> {
    @Override
    public List<Double> convert(List<Double> input, Double[] arguments, RandomGenerator randomGenerator) {
        List<Double> result = new ArrayList<>(input);
        Collections.shuffle(result);
        return result;
    }

    @Override
    public ConverterType getName() {
        return ConverterType.SHUFFLE;
    }
}
