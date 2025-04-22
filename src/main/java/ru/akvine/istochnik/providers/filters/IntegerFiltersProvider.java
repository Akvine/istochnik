package ru.akvine.istochnik.providers.filters;

import ru.akvine.istochnik.enums.FilterType;
import ru.akvine.istochnik.services.filters.integer.IntegerFilter;

import java.util.Map;

public record IntegerFiltersProvider(Map<FilterType, IntegerFilter<Integer, Double>> filters) {
    public IntegerFilter<Integer, Double> getFilter(FilterType type) {
        return filters.get(type);
    }
}
