package ru.akvine.istochnik.providers.converters;

import ru.akvine.istochnik.enums.ConverterType;
import ru.akvine.istochnik.services.converters.string.StringConverter;

import java.util.Map;

public record StringConvertersProvider(Map<ConverterType, StringConverter<String, String>> converters) {

    public StringConverter<String, String> getConverter(ConverterType converterName) {
        return converters.get(converterName);
    }

}
