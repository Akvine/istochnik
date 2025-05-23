package ru.akvine.istochnik.providers;

import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;
import ru.akvine.istochnik.services.generators.Config;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

import java.util.Map;

public record ConfigMapperServicesProvider(Map<String, ConfigMapperService<? extends Config>> configMappers) {

    public ConfigMapperService<? extends Config> get(String type) {
        Asserts.isNotNull(type);
        if (configMappers.containsKey(type)) {
            return configMappers.get(type);
        }

        throw new UnsupportedTypeGenerationException("Config mapper = [" + type + "] is not supported");
    }
}
