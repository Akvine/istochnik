package ru.akvine.istochnik.services.filters.doubles;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CeilDoubleFilter extends DoubleFilter<Double, Double>{
    @Override
    public List<Double> filter(List<Double> input, Double[] arguments) {
        return List.of();
    }

    @Override
    public String getName() {
        return "ceil";
    }
}
