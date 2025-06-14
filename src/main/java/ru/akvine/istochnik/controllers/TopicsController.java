package ru.akvine.istochnik.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.akvine.compozit.commons.dto.Response;
import ru.akvine.istochnik.controllers.dto.converters.TopicsConverter;
import ru.akvine.istochnik.controllers.meta.TopicsControllerMeta;
import ru.akvine.istochnik.enums.Language;

@RestController
@RequiredArgsConstructor
public class TopicsController implements TopicsControllerMeta {
    private final TopicsConverter topicsConverter;

    @Override
    public Response list(String lang) {
        return topicsConverter.convertToListTopicsResponse(Language.from(lang));
    }
}
