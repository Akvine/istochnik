package ru.akvine.istochnik.managers;

import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;
import ru.akvine.istochnik.services.CustomTypeGeneratorService;

import java.util.Map;

public record CustomTypeGeneratorServicesManager(Map<CustomType, CustomTypeGeneratorService> generatorServices) {
    public CustomTypeGeneratorService get(CustomType type) {
        Asserts.isNotNull(type);
        if (generatorServices.containsKey(type)) {
            return generatorServices.get(type);
        }

        throw new UnsupportedTypeGenerationException("Custom type generator service = [" + type + "] is not supported");
    }
}
