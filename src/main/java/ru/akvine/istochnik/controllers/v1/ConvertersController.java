package ru.akvine.istochnik.controllers.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.akvine.istochnik.controllers.converters.ConvertersMapper;
import ru.akvine.istochnik.controllers.dto.Response;
import ru.akvine.istochnik.controllers.v1.meta.ConvertersControllerMeta;
import ru.akvine.istochnik.enums.Language;

@RestController
@RequiredArgsConstructor
public class ConvertersController implements ConvertersControllerMeta {
    private final ConvertersMapper convertersMapper;

    @Override
    public Response list(String lang) {
        return convertersMapper.convertToConvertersListResponse(Language.from(lang));
    }
}
