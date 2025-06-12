package ru.akvine.istochnik.providers.converters;

import ru.akvine.istochnik.enums.ConverterType;
import ru.akvine.istochnik.services.converters.integer.IntegerConverter;

import java.util.Map;

public record IntegerConvertersProvider(Map<ConverterType, IntegerConverter<Integer, Double>> converters) {
    public IntegerConverter<Integer, Double> getConverter(ConverterType type) {
        return converters.get(type);
    }
}
