package ru.akvine.istochnik.services.converters.string;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;

@Service
public class RepeatConverter extends StringConverter<String, String> {
    @Override
    public List<String> convert(List<String> input, String[] arguments) {
        int count;
        if (arguments.length != 0 && StringUtils.isNotBlank(arguments[0])) {
            count = Integer.parseInt(arguments[0]);
        } else {
            count = 1;
        }

        return input.stream().map(value -> String.valueOf(value).repeat(Math.max(0, count))).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.REPEAT;
    }
}
