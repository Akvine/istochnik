package ru.akvine.istochnik.providers.filters;

import ru.akvine.istochnik.enums.FilterType;
import ru.akvine.istochnik.services.filters.string.StringFilter;

import java.util.Map;

public record StringFiltersProvider(Map<FilterType, StringFilter<String, String>> filters) {

    public StringFilter<String, String> getFilter(FilterType filterName) {
        return filters.get(filterName);
    }

}
