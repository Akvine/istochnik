package ru.akvine.istochnik.services.filters.integer;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CosIntegerFilter extends IntegerFilter<Integer, Void> {
    @Override
    public List<Integer> filter(List<Integer> input, Void[] arguments) {
        return input.stream().map(value -> (int) Math.cos(value)).toList();
    }

    @Override
    public String getName() {
        return "cos";
    }
}
