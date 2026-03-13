package ru.akvine.istochnik.controllers.v1.meta;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;

@RequestMapping(value = "/v1/generator")
public interface GeneratorControllerMeta {
    @PostMapping
    ResponseEntity<?> generate(@RequestBody @Valid GenerateTableRequest request);
}
