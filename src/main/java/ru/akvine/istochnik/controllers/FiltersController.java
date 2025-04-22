package ru.akvine.istochnik.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.akvine.istochnik.controllers.converters.FiltersConverter;
import ru.akvine.istochnik.controllers.dto.common.Response;
import ru.akvine.istochnik.controllers.meta.FiltersControllerMeta;

@RestController
@RequiredArgsConstructor
public class FiltersController implements FiltersControllerMeta {
    private final FiltersConverter filtersConverter;

    @Override
    public Response list() {
        return filtersConverter.convertToFiltersListResponse();
    }
}
