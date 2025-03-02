package ru.akvine.istochnik.services.filters.doubles;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CosDoubleFilter extends DoubleFilter<Double, Double> {
    @Override
    public List<Double> filter(List<Double> input, Double[] arguments) {
        return input.stream().map(Math::cos).toList();
    }

    @Override
    public String getName() {
        return "cos";
    }
}
