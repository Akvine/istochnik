package ru.akvine.istochnik.managers;

import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;
import ru.akvine.istochnik.services.BaseTypeGeneratorService;
import ru.akvine.istochnik.utils.Asserts;

import java.util.Map;

public record BaseTypeGeneratorServicesManager(Map<BaseType, BaseTypeGeneratorService> generatorServices) {
    public BaseTypeGeneratorService get(BaseType type) {
        Asserts.isNotNull(type);
        if (generatorServices.containsKey(type)) {
            return generatorServices.get(type);
        }

        throw new UnsupportedTypeGenerationException("Base type generator service = [" + type + "] is not supported");
    }
}
