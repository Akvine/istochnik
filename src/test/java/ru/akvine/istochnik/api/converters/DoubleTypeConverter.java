package ru.akvine.istochnik.api.converters;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.akvine.compozit.commons.utils.MathUtils;
import ru.akvine.istochnik.enums.BaseType;

import java.util.List;

@Component
public class DoubleTypeConverter implements TypeConverter<Double> {
    @Override
    public List<Double> convert(byte[] response) {
        List<String> lines = asString(response);
        return lines.stream()
                .map(value -> {
                    if (StringUtils.isBlank(value)) {
                        return null;
                    } else {
                        return Double.parseDouble(value);
                    }
                })
                .map(value -> {
                    if (value == null) {
                        return null;
                    } else {
                        return MathUtils.round(value, 3);
                    }
                })
                .toList();
    }

    @Override
    public BaseType getByType() {
        return BaseType.DOUBLE;
    }
}
