package ru.akvine.istochnik.managers.filters;

import ru.akvine.istochnik.services.filters.string.StringFilter;

import java.util.Map;

public record StringFiltersManager(Map<String, StringFilter<String, String>> filters) {
    public StringFilter<String, String> getByType(String type) {
        if (filters.containsKey(type)) {
            return filters.get(type);
        }

        throw new UnsupportedOperationException("Filter with name = [" + type + "] is not supported!");
    }
}
