package ru.akvine.istochnik.controllers.meta;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.akvine.istochnik.controllers.dto.common.Response;

@RequestMapping(value = "/types")
public interface TypesControllerMeta {
    @GetMapping(value = "/custom")
    Response list();
}
