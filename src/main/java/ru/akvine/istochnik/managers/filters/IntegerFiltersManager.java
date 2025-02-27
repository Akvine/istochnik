package ru.akvine.istochnik.managers.filters;

import ru.akvine.istochnik.services.filters.integer.IntegerFilter;

import java.util.Map;

public record IntegerFiltersManager(Map<String, IntegerFilter<Integer, Double>> filters) {

    public IntegerFilter<Integer, Double> getByType(String type) {
        if (filters.containsKey(type)) {
            return filters.get(type);
        }

        throw new UnsupportedOperationException("Filter with name = [" + type + "] is not supported!");
    }
}
