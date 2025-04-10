package ru.akvine.istochnik.services.filters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FilterType;

import java.util.List;

@Service
public class ReplaceAllFilter extends StringFilter<String, String> {
    @Override
    public List<String> filter(List<String> input, String[] arguments) {
        return input.stream().map(value -> value.replaceAll(arguments[0], arguments[1])).toList();
    }

    @Override
    public FilterType getName() {
        return FilterType.REPLACE_ALL;
    }
}
