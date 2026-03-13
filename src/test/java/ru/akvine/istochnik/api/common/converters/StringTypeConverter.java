package ru.akvine.istochnik.api.common.converters;

import java.util.List;
import org.springframework.stereotype.Component;
import ru.akvine.istochnik.enums.BaseType;

@Component
public class StringTypeConverter implements TypeConverter<String> {
    @Override
    public List<String> convert(byte[] response) {
        return asString(response);
    }

    @Override
    public BaseType getByType() {
        return BaseType.STRING;
    }
}
