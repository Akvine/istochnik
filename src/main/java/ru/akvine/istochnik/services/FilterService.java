package ru.akvine.istochnik.services;

import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.services.dto.Filter;

import java.util.List;

public interface FilterService {
    List<?> apply(List<?> generatedValues, List<Filter> filters);

    BaseType getType();
}
