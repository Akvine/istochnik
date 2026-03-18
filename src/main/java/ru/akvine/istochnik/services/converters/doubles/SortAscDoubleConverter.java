package ru.akvine.istochnik.services.converters.doubles;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.random.RandomGenerator;

@Service
public class SortAscDoubleConverter extends DoubleConverter<Double, Double> {
    @Override
    public List<Double> convert(List<Double> input,
                                Double[] arguments,
                                RandomGenerator randomGenerator,
                                double probability) {
        List<Double> result = new ArrayList<>(input);
        result.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
        return result;
    }

    @Override
    public ConverterType getName() {
        return ConverterType.SORT_ASC;
    }
}
