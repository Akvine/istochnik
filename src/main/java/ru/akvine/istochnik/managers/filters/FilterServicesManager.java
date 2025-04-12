package ru.akvine.istochnik.managers.filters;

import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;
import ru.akvine.istochnik.services.FilterService;

import java.util.Map;

public record FilterServicesManager(Map<BaseType, FilterService> filterServices) {
    public FilterService getByType(BaseType type) {
        if (filterServices.containsKey(type)) {
            return filterServices.get(type);
        }

        throw new UnsupportedTypeGenerationException("Filter service with type = [" + type + "] is not supported");
    }
}
