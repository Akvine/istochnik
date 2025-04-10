package ru.akvine.istochnik.services.filters.integer;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FilterType;

import java.util.List;

@Service
public class SinIntegerFilter extends IntegerFilter<Integer, Double> {
    @Override
    public List<Integer> filter(List<Integer> input, Double[] argument) {
        return input.stream().map(value -> (int) Math.sin(value)).toList();
    }

    @Override
    public FilterType getName() {
        return FilterType.SIN;
    }
}
