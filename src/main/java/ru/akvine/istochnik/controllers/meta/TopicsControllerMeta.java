package ru.akvine.istochnik.controllers.meta;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.akvine.compozit.commons.dto.Response;

@RequestMapping(value = "/topics")
public interface TopicsControllerMeta {
    @GetMapping
    Response list(@RequestParam(required = false, defaultValue = "ru") String lang);
}
