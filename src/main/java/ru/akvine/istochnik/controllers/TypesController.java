package ru.akvine.istochnik.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.akvine.compozit.commons.dto.Response;
import ru.akvine.istochnik.controllers.converters.TypesConverter;
import ru.akvine.istochnik.controllers.meta.TypesControllerMeta;
import ru.akvine.istochnik.enums.Language;

@RestController
@RequiredArgsConstructor
public class TypesController implements TypesControllerMeta {
    private final TypesConverter typesConverter;

    @Override
    public Response listCustom() {
        return typesConverter.convertToCustomTypesListResponse();
    }

    @Override
    public Response listBase(@RequestParam(required = false, defaultValue = "en") String lang) {
        return typesConverter.convertToBaseTypesListResponse(Language.from(lang));
    }
}
