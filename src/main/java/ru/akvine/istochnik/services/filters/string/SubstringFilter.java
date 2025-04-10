package ru.akvine.istochnik.services.filters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FilterType;

import java.util.List;

@Service
public class SubstringFilter extends StringFilter<String, String> {
    @Override
    public List<String> filter(List<String> input, String[] arguments) {
        int beginIndex = Integer.parseInt(arguments[0]);
        int endIndex = Integer.parseInt(arguments[1]);

        return input.stream().map(value -> value.substring(beginIndex, endIndex)).toList();
    }

    @Override
    public FilterType getName() {
        return FilterType.SUBSTRING;
    }
}
