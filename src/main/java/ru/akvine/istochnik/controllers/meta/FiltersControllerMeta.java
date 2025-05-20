package ru.akvine.istochnik.controllers.meta;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.akvine.compozit.commons.dto.Response;

@RequestMapping(value = "/filters")
public interface FiltersControllerMeta {
    @GetMapping
    Response list();
}
