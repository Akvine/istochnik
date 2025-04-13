package ru.akvine.istochnik.services.filters.string;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FilterType;

import java.util.List;

@Service
public class RepeatFilter extends StringFilter<String, String> {
    @Override
    public List<String> filter(List<String> input, String[] arguments) {
        int count;
        if (arguments.length != 0 && StringUtils.isNotBlank(arguments[0])) {
            count = Integer.parseInt(arguments[0]);
        } else {
            count = 1;
        }

        return input.stream().map(value -> String.valueOf(value).repeat(Math.max(0, count))).toList();
    }

    @Override
    public FilterType getName() {
        return FilterType.REPEAT;
    }
}
