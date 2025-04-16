package ru.akvine.istochnik.services.filters.integer;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FilterType;

import java.util.List;

@Service
public class ModIntegerFilter extends IntegerFilter<Integer, Double> {
    @Override
    public List<Integer> filter(List<Integer> input, Double[] arguments) {
        Double operand = arguments[0];
        return input.stream().map(value -> (int) (value % operand)).toList();
    }

    @Override
    public FilterType getName() {
        return FilterType.MOD;
    }
}
