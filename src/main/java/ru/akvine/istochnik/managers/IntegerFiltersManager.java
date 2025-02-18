package ru.akvine.istochnik.managers;

import ru.akvine.istochnik.services.filters.integer.AbstractIntegerFilter;

import java.util.Map;

public record IntegerFiltersManager(Map<String, AbstractIntegerFilter<Integer>> filters) {

    public AbstractIntegerFilter<Integer> getByType(String type) {
        if (filters.containsKey(type)) {
            return filters.get(type);
        }

        throw new UnsupportedOperationException("Type = [" + type + "] is not supported!");
    }
}
