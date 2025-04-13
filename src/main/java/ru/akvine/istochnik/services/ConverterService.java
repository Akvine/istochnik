package ru.akvine.istochnik.services;

import ru.akvine.istochnik.services.dto.Filter;

import java.util.List;

public interface ConverterService {
    List<String> convert(List<?> inputValues, List<Filter> filtersToApply);
}
