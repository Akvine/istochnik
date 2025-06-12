package ru.akvine.istochnik.providers.converters;

import ru.akvine.istochnik.enums.ConverterType;
import ru.akvine.istochnik.services.converters.doubles.DoubleConverter;

import java.util.Map;

public record DoubleConvertersProvider(Map<ConverterType, DoubleConverter<Double, Double>> converters) {
    public DoubleConverter<Double, Double> getConverter(ConverterType converterName) {
        return converters.get(converterName);
    }
}
