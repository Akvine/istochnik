package ru.akvine.istochnik.services.filters.integer;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FilterType;
import ru.akvine.istochnik.services.filters.Filter;

import java.util.List;

@Service
public class AbsIntegerFilter extends Filter<Integer, Void> {
    @Override
    public List<Integer> filter(List<Integer> input, Void[] argument) {
        return input.stream().map(Math::abs).toList();
    }

    @Override
    public FilterType getName() {
        return FilterType.ABS;
    }
}
