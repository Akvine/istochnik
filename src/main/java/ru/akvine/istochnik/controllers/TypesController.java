package ru.akvine.istochnik.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.akvine.istochnik.controllers.converters.TypesConverter;
import ru.akvine.istochnik.controllers.dto.common.Response;
import ru.akvine.istochnik.controllers.meta.TypesControllerMeta;

@RestController
@RequiredArgsConstructor
public class TypesController implements TypesControllerMeta {
    private final TypesConverter typesConverter;

    @Override
    public Response list() {
        return typesConverter.convertToCustomTypesListResponse();
    }
}
