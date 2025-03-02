package ru.akvine.istochnik.services.filters.doubles;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AbsDoubleFilter extends DoubleFilter<Double, Double> {
    @Override
    public List<Double> filter(List<Double> input, Double[] arguments) {
        return input.stream().map(Math::abs).toList();
    }

    @Override
    public String getName() {
        return "abs";
    }
}
