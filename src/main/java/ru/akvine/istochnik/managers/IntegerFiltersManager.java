package ru.akvine.istochnik.managers;

import ru.akvine.istochnik.services.filters.integer.IntegerFilter;

import java.util.Map;

public record IntegerFiltersManager(Map<String, IntegerFilter<Integer, Double>> filters) {

    public IntegerFilter<Integer, Double> getByType(String type) {
        if (filters.containsKey(type)) {
            return filters.get(type);
        }

        throw new UnsupportedOperationException("Type = [" + type + "] is not supported!");
    }
}
