package ru.akvine.istochnik.controllers.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.akvine.compozit.commons.dto.Response;
import ru.akvine.istochnik.controllers.converters.GenerationStrategiesConverter;
import ru.akvine.istochnik.controllers.v1.meta.GenerationStrategiesControllerMeta;
import ru.akvine.istochnik.enums.Language;

@RestController
@RequiredArgsConstructor
public class GenerationStrategiesController implements GenerationStrategiesControllerMeta {
    private final GenerationStrategiesConverter converter;

    @Override
    // TODO: настройка default.language вообще нигде не используется. Нужно добавить как значение по умолчанию для всего
    // приложения
    public Response list(String lang) {
        return converter.convertToStrategiesListResponse(Language.from(lang));
    }
}
