package ru.akvine.istochnik.services.filters.doubles;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FilterType;

import java.util.List;

@Service
public class FloorDoubleFilter extends DoubleFilter<Double, Double> {
    @Override
    public List<Double> filter(List<Double> input, Double[] arguments) {
        return input.stream().map(Math::floor).toList();
    }

    @Override
    public FilterType getName() {
        return FilterType.FLOOR;
    }
}
