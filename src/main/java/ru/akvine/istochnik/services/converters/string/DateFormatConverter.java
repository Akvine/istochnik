package ru.akvine.istochnik.services.converters.string;

import org.springframework.stereotype.Service;
import ru.akvine.compozit.commons.utils.DateTimeUtils;
import ru.akvine.istochnik.enums.ConverterType;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DateFormatConverter extends StringConverter<String, String> {
    @Override
    public List<String> convert(List<String> input, String[] arguments) {
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
    public ConverterType getName() {
        return ConverterType.DATE_FORMAT;
    }
}
