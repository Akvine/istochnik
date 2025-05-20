package ru.akvine.istochnik.controllers.meta;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.akvine.compozit.commons.dto.Response;

@RequestMapping(value = "/types")
public interface TypesControllerMeta {
    @GetMapping(value = "/custom")
    Response listCustom();

    @GetMapping(value = "/base")
    Response listBase(@RequestParam(required = false) String lang);
}
