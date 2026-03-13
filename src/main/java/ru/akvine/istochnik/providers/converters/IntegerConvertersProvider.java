package ru.akvine.istochnik.providers.converters;

import java.util.Map;
import ru.akvine.istochnik.enums.ConverterType;
import ru.akvine.istochnik.services.converters.integer.IntegerConverter;

public record IntegerConvertersProvider(Map<ConverterType, IntegerConverter<Long, Double>> converters) {
    public IntegerConverter<Long, Double> getConverter(ConverterType type) {
        return converters.get(type);
    }
}
