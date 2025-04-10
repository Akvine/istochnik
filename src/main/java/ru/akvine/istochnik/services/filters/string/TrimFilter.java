package ru.akvine.istochnik.services.filters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FilterType;

import java.util.List;

@Service
public class TrimFilter extends StringFilter<String, String> {
    @Override
    public List<String> filter(List<String> input, String[] arguments) {
        return input.stream().map(String::trim).toList();
    }

    @Override
    public FilterType getName() {
        return FilterType.TRIM;
    }
}
