package ru.akvine.istochnik.services.converters.string;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

@Service
public class ShuffleConverter extends StringConverter<String, String> {
    @Override
    public List<String> convert(
            List<String> input, String[] arguments, RandomGenerator randomGenerator, double probability) {
        List<String> result = new ArrayList<>(input);
        Collections.shuffle(result);
        return result;
    }

    @Override
    public ConverterType getName() {
        return ConverterType.SHUFFLE;
    }
}
