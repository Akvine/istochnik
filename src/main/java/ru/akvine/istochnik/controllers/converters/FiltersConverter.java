package ru.akvine.istochnik.controllers.converters;

import org.springframework.stereotype.Component;
import ru.akvine.istochnik.controllers.dto.filters.FilterTypeDto;
import ru.akvine.istochnik.controllers.dto.filters.FiltersListResponse;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.FilterType;

import java.util.Arrays;

@Component
public class FiltersConverter {
    public FiltersListResponse convertToFiltersListResponse() {
        return new FiltersListResponse()
                .setFilters(Arrays.stream(BaseType.values()).map(this::buildFilterDto).toList());
    }

    private FilterTypeDto buildFilterDto(BaseType type) {
        return new FilterTypeDto()
                .setType(type.getValue())
                .setSize(type.getSupportedFilterType().size())
                .setNames(type.getSupportedFilterType().stream()
                        .map(FilterType::getName)
                        .toList());
    }
}
