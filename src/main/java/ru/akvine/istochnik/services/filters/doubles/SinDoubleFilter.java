package ru.akvine.istochnik.services.filters.doubles;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FilterType;

import java.util.List;

@Service
public class SinDoubleFilter extends DoubleFilter<Double, Double> {
    @Override
    public List<Double> filter(List<Double> input, Double[] arguments) {
        return input.stream().map(Math::sin).toList();
    }

    @Override
    public FilterType getName() {
        return FilterType.SIN;
    }
}
