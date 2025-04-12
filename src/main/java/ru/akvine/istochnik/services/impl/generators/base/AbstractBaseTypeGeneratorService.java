package ru.akvine.istochnik.services.impl.generators.base;

import ru.akvine.istochnik.managers.ConfigMapperServicesManager;
import ru.akvine.istochnik.managers.filters.FilterServicesManager;
import ru.akvine.istochnik.services.BaseTypeGeneratorService;
import ru.akvine.istochnik.services.dto.Filter;

import java.util.List;

public abstract class AbstractBaseTypeGeneratorService implements BaseTypeGeneratorService {
    protected final ConfigMapperServicesManager configMappersManager;
    protected final FilterServicesManager filterServicesManager;

    protected AbstractBaseTypeGeneratorService(ConfigMapperServicesManager configMappersManager, FilterServicesManager filterServicesManager) {
        this.configMappersManager = configMappersManager;
        this.filterServicesManager = filterServicesManager;
    }

    protected List<?> apply(List<?> generatedValues, List<Filter> filters) {
        return filterServicesManager.getByType(getType()).apply(generatedValues, filters);
    }
}
