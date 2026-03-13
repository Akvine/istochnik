package ru.akvine.istochnik.services.converters.doubles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

@Service
public class ShuffleDoubleConverter extends DoubleConverter<Double, Double> {
    @Override
    public List<Double> convert(
            List<Double> input, Double[] arguments, RandomGenerator randomGenerator, double probability) {
        List<Double> result = new ArrayList<>(input);
        Collections.shuffle(result);
        return result;
    }

    @Override
    public ConverterType getName() {
        return ConverterType.SHUFFLE;
    }
}
