package ru.akvine.istochnik.services.converters.integer;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.random.RandomGenerator;

@Service
public class SortAscIntegerConverter extends IntegerConverter<Long, Double> {
    @Override
    public List<Long> convert(List<Long> input, Double[] arguments, RandomGenerator randomGenerator, double probability) {
        List<Long> result = new ArrayList<>(input);
        result.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
        return result;
    }

    @Override
    public ConverterType getName() {
        return ConverterType.SORT_ASC;
    }
}
