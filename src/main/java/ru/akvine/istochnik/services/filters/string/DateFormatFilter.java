package ru.akvine.istochnik.services.filters.string;

import org.springframework.stereotype.Service;
import ru.akvine.compozit.commons.utils.DateTimeUtils;
import ru.akvine.istochnik.enums.FilterType;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DateFormatFilter extends StringFilter<String, String> {
    @Override
    public List<String> filter(List<String> input, String[] arguments) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern(arguments[0]);

        return input.stream().map(value -> {
                    if (DateTimeUtils.isDate(value)) {
                        DateTimeFormatter originalFormatter = DateTimeUtils.extractFromLocalDate(value);
                        if (originalFormatter == null) {
                            return value;
                        }

                        return DateTimeUtils.toLocalDate(value, originalFormatter).format(format);
                    }
                    if (DateTimeUtils.isDateTime(value)) {
                        DateTimeFormatter originalFormatter = DateTimeUtils.extractFromLocalDateTime(value);
                        if (originalFormatter == null) {
                            return value;
                        }

                        return DateTimeUtils.toLocalDateTime(value, originalFormatter).format(format);
                    }

                    return value;
                })
                .toList();
    }

    @Override
    public FilterType getName() {
        return FilterType.DATE_FORMAT;
    }
}
