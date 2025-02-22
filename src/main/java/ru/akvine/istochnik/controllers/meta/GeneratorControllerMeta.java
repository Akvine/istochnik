package ru.akvine.istochnik.controllers.meta;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.services.dto.Table;

@RequestMapping(value = "/generator")
public interface GeneratorControllerMeta {
    @GetMapping
    ResponseEntity<?> generate(@RequestBody @Valid GenerateTableRequest request);
}
