package ru.akvine.istochnik.services.converters.integer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

@Service
public class ShuffleIntegerConverter extends IntegerConverter<Long, Double> {

    @Override
    public List<Long> convert(
            List<Long> input, Double[] arguments, RandomGenerator randomGenerator, double probability) {
        List<Long> result = new ArrayList<>(input);
        Collections.shuffle(result, randomGenerator);
        return result;
    }

    @Override
    public ConverterType getName() {
        return ConverterType.SHUFFLE;
    }
}
