package ru.akvine.istochnik.controllers.v1.meta;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.akvine.compozit.commons.dto.Response;

@RequestMapping(value = "/v1/converters")
public interface ConvertersControllerMeta {
    @GetMapping
    Response list();
}
