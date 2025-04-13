package ru.akvine.istochnik.services.filters.string;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FilterType;

import java.util.List;

@Service
public class ReverseFilter extends StringFilter<String, String> {
    @Override
    public List<String> filter(List<String> input, String[] arguments) {
        return input.stream().map(StringUtils::reverse).toList();
    }

    @Override
    public FilterType getName() {
        return FilterType.REVERSE;
    }
}
