package ru.akvine.istochnik.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.akvine.istochnik.controllers.converters.GeneratorConverter;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.controllers.meta.GeneratorControllerMeta;
import ru.akvine.istochnik.services.GeneratorFacade;
import ru.akvine.istochnik.services.dto.GenerateData;
import ru.akvine.istochnik.services.dto.Table;

@RestController
@RequiredArgsConstructor
public class GeneratorController implements GeneratorControllerMeta {
    private final GeneratorConverter generatorConverter;
    private final GeneratorFacade generatorFacade;

    @Override
    public Table generate(@RequestBody @Valid GenerateTableRequest request) {
        GenerateData generateData = generatorConverter.convertToGenerateData(request);
        return generatorFacade.generate(generateData);
    }
}
