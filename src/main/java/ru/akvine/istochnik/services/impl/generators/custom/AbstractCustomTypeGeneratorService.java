package ru.akvine.istochnik.services.impl.generators.custom;

import ru.akvine.istochnik.managers.ConfigMapperServicesManager;
import ru.akvine.istochnik.services.CustomTypeGeneratorService;

public abstract class AbstractCustomTypeGeneratorService implements CustomTypeGeneratorService {
    protected final ConfigMapperServicesManager configMappersManager;

    protected AbstractCustomTypeGeneratorService(ConfigMapperServicesManager configMappersManager) {
        this.configMappersManager = configMappersManager;
    }
}
