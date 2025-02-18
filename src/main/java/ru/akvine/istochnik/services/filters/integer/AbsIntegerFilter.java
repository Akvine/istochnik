package ru.akvine.istochnik.services.filters.integer;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.filters.NumberFilter;

import java.util.List;

@Service
public class AbsIntegerFilter extends NumberFilter<Integer> {
    @Override
    public List<Integer> filter(List<Integer> input, double argument) {
        return input.stream().map(Math::abs).toList();
    }

    @Override
    public String getName() {
        return "abs";
    }
}
