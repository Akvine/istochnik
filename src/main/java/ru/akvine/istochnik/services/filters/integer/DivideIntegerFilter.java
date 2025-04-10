package ru.akvine.istochnik.services.filters.integer;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FilterType;

import java.util.List;

@Service
public class DivideIntegerFilter extends IntegerFilter<Integer, Double> {
    @Override
    public List<Integer> filter(List<Integer> input, Double[] arguments) {
        return input.stream().map(value -> (int) (value / arguments[0])).toList();
    }

    @Override
    public FilterType getName() {
        return FilterType.DIVIDE;
    }
}
