package ru.akvine.istochnik.services.filters.integer;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PowIntegerFilter extends AbstractIntegerFilter<Integer> {
    @Override
    public List<Integer> filter(List<Integer> input, double argument) {
        return input.stream().map(value -> (int) Math.pow(value, argument)).toList();
    }

    @Override
    public String getName() {
        return "pow";
    }
}
