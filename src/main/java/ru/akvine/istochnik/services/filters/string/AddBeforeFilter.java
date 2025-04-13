package ru.akvine.istochnik.services.filters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FilterType;

import java.util.List;

@Service
public class AddBeforeFilter extends StringFilter<String, String> {
    @Override
    public List<String> filter(List<String> input, String[] arguments) {
        String arg = arguments[0];
        return input.stream().map(value -> arg + value).toList();
    }

    @Override
    public FilterType getName() {
        return FilterType.ADD_BEFORE;
    }
}
