package ru.akvine.istochnik.managers.filters;

import ru.akvine.istochnik.services.filters.doubles.DoubleFilter;

import java.util.Map;

public record DoubleFiltersManager(Map<String, DoubleFilter<Double, Double>> filters) {
    public DoubleFilter<Double, Double> getFilter(String filterName) {
        if (filters.containsKey(filterName)) {
            return filters.get(filterName);
        }

        throw new UnsupportedOperationException("Filter with name = [" + filterName + "] is not supported!");
    }
}
