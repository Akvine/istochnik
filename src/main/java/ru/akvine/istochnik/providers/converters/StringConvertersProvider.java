package ru.akvine.istochnik.providers.converters;

import java.util.Map;
import ru.akvine.istochnik.enums.ConverterType;
import ru.akvine.istochnik.services.converters.string.StringConverter;

public record StringConvertersProvider(Map<ConverterType, StringConverter<String, String>> converters) {

    public StringConverter<String, String> getConverter(ConverterType converterName) {
        return converters.get(converterName);
    }
}
