package ru.akvine.istochnik.managers.filters;

import ru.akvine.istochnik.services.filters.integer.IntegerFilter;

import java.util.Map;

public record IntegerFiltersManager(Map<String, IntegerFilter<Integer, Double>> filters) {
    public IntegerFilter<Integer, Double> getFilter(String filterName) {
        if (filters.containsKey(filterName)) {
            return filters.get(filterName);
        }

        throw new UnsupportedOperationException("Filter with name = [" + filterName + "] is not supported!");
    }
}
