package ru.akvine.istochnik.controllers;

import org.springframework.web.bind.annotation.RestController;
import ru.akvine.istochnik.controllers.meta.GeneratorControllerMeta;
import ru.akvine.istochnik.services.generators.Config;
import ru.akvine.istochnik.services.generators.uuid.UuidGeneratorService;

import java.util.List;

@RestController
public class GeneratorController implements GeneratorControllerMeta {
    private final UuidGeneratorService uuidGeneratorService;

    public GeneratorController(UuidGeneratorService uuidGeneratorService) {
        this.uuidGeneratorService = uuidGeneratorService;
    }

    @Override
    public List<?> make() {
        Config config = new Config(10);
        return uuidGeneratorService.generate(config, "100");
    }
}
