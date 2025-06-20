package ru.akvine.istochnik.api.converters;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.akvine.istochnik.enums.BaseType;

import java.util.List;

@Component
public class IntegerTypeConverter implements TypeConverter<Long> {
    @Override
    public List<Long> convert(byte[] response) {
        List<String> lines = asString(response);

        List<String> values = asString(response);
        return values.stream()
                .map(value -> {
                    if (StringUtils.isBlank(value)) {
                        return null;
                    } else {
                        return Long.parseLong(value);
                    }
                })
                .toList();
    }

    @Override
    public BaseType getByType() {
        return BaseType.INTEGER;
    }
}
