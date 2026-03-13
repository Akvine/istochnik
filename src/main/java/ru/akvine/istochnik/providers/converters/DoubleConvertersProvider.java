package ru.akvine.istochnik.providers.converters;

import java.util.Map;
import ru.akvine.istochnik.enums.ConverterType;
import ru.akvine.istochnik.services.converters.doubles.DoubleConverter;

public record DoubleConvertersProvider(Map<ConverterType, DoubleConverter<Double, Double>> converters) {
    public DoubleConverter<Double, Double> getConverter(ConverterType converterName) {
        return converters.get(converterName);
    }
}
