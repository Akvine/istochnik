package ru.akvine.istochnik.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.akvine.compozit.commons.dto.Response;
import ru.akvine.istochnik.controllers.converters.ConvertersMapper;
import ru.akvine.istochnik.controllers.meta.ConvertersControllerMeta;

@RestController
@RequiredArgsConstructor
public class ConvertersController implements ConvertersControllerMeta {
    private final ConvertersMapper convertersMapper;

    @Override
    public Response list() {
        return convertersMapper.convertToConvertersListResponse();
    }
}
