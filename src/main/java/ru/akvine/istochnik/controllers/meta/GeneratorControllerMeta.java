package ru.akvine.istochnik.controllers.meta;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;

@RequestMapping(value = "/generator")
public interface GeneratorControllerMeta {
    @PostMapping
    ResponseEntity<?> generate(@RequestBody @Valid GenerateTableRequest request);
}
