package ru.akvine.istochnik.controllers.v1.meta;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.akvine.compozit.commons.dto.Response;

@RequestMapping(value = "/v1/strategies")
public interface GenerationStrategiesControllerMeta {
    @GetMapping
    Response list(@RequestParam(required = false, defaultValue = "en") String lang);
}
