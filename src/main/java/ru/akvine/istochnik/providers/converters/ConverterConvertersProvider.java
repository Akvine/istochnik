package ru.akvine.istochnik.providers.converters;

import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;
import ru.akvine.istochnik.services.ConverterService;

import java.util.Map;

public record ConverterConvertersProvider(Map<BaseType, ConverterService> converterServices) {
    public ConverterService getByType(BaseType type) {
        if (converterServices.containsKey(type)) {
            return converterServices.get(type);
        }

        throw new UnsupportedTypeGenerationException("Converter service with type = [" + type + "] is not supported");
    }
}
