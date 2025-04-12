package ru.akvine.istochnik.services.filters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FilterType;

import java.util.List;

@Service
public class RemoveWhitespacesFilter extends StringFilter<String, String> {
    @Override
    public List<String> filter(List<String> input, String[] arguments) {
        return input.stream().map(value -> value.replaceAll("\\\\s+", "")).toList();
    }

    @Override
    public FilterType getName() {
        return FilterType.REMOVE_WHITESPACES;
    }
}
