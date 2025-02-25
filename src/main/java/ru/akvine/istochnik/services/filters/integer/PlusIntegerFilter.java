package ru.akvine.istochnik.services.filters.integer;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlusIntegerFilter extends IntegerFilter<Integer, Double> {
    @Override
    public List<Integer> filter(List<Integer> input, Double[] argument) {
        return input.stream().map(value -> (int) (value + argument[0])).toList();
    }

    @Override
    public String getName() {
        return "plus";
    }
}
