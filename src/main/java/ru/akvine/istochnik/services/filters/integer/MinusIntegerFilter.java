package ru.akvine.istochnik.services.filters.integer;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MinusIntegerFilter extends AbstractIntegerFilter<Integer> {
    @Override
    public List<Integer> filter(List<Integer> input, double argument) {
        return input.stream().map(value -> (int) (value - argument)).toList();
    }

    @Override
    public String getName() {
        return "minus";
    }
}
