package ru.akvine.istochnik.services.filters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FilterType;

import java.util.List;

@Service
public class UpperCaseFilter extends StringFilter<String, String> {
    @Override
    public List<String> filter(List<String> input, String[] arguments) {
        return input.stream().map(String::toUpperCase).toList();
    }

    @Override
    public FilterType getName() {
        return FilterType.UPPER_CASE;
    }
}
