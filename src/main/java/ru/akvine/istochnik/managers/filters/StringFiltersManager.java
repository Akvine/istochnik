package ru.akvine.istochnik.managers.filters;

import ru.akvine.istochnik.services.filters.string.StringFilter;

import java.util.Map;

public record StringFiltersManager(Map<String, StringFilter<String, String>> filters) {

    public StringFilter<String, String> getFilter(String filterName) {
        if (filters.containsKey(filterName)) {
            return filters.get(filterName);
        }

        throw new UnsupportedOperationException("Filter with name = [" + filterName + "] is not supported!");
    }

}
