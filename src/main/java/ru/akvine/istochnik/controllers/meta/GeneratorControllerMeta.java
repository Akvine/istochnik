package ru.akvine.istochnik.controllers.meta;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.akvine.istochnik.controllers.dto.GenerateRequest;

import java.util.List;

@RequestMapping(value = "/generator")
public interface GeneratorControllerMeta {
    @GetMapping
    List<?> make(@RequestBody GenerateRequest request);
}
