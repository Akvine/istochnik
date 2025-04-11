package ru.akvine.istochnik.services.impl.generators.base;

import ru.akvine.istochnik.managers.ConfigMapperServicesManager;
import ru.akvine.istochnik.services.BaseTypeGeneratorService;

public abstract class AbstractBaseTypeGeneratorService implements BaseTypeGeneratorService {
    protected final ConfigMapperServicesManager configMappersManager;

    protected AbstractBaseTypeGeneratorService(ConfigMapperServicesManager configMappersManager) {
        this.configMappersManager = configMappersManager;
    }
}
