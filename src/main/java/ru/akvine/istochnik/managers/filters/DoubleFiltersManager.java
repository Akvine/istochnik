package ru.akvine.istochnik.managers.filters;

import ru.akvine.istochnik.enums.FilterType;
import ru.akvine.istochnik.services.filters.doubles.DoubleFilter;

import java.util.Map;

public record DoubleFiltersManager(Map<FilterType, DoubleFilter<Double, Double>> filters) {
    public DoubleFilter<Double, Double> getFilter(FilterType filterName) {
        return filters.get(filterName);
    }
}
