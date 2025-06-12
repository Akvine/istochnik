package ru.akvine.istochnik.services;

import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.services.dto.Converter;

import java.util.List;

public interface ConverterService {
    List<?> apply(List<?> generatedValues, List<Converter> converters);

    BaseType getType();
}
