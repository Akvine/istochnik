package ru.akvine.istochnik.services.filters.doubles;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PowDoubleFilter extends DoubleFilter<Double, Double> {
    @Override
    public List<Double> filter(List<Double> input, Double[] arguments) {
        return input.stream().map(value -> Math.pow(value, arguments[0])).toList();
    }

    @Override
    public String getName() {
        return "pow";
    }
}
