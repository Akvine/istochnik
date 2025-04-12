package ru.akvine.istochnik.services.impl.generators.custom;

import ru.akvine.istochnik.managers.ConfigMapperServicesManager;
import ru.akvine.istochnik.managers.filters.FilterServicesManager;
import ru.akvine.istochnik.services.CustomTypeGeneratorService;
import ru.akvine.istochnik.services.dto.Filter;

import java.util.List;

public abstract class AbstractCustomTypeGeneratorService implements CustomTypeGeneratorService {
    protected final ConfigMapperServicesManager configMappersManager;
    protected final FilterServicesManager filterServicesManager;

    protected AbstractCustomTypeGeneratorService(ConfigMapperServicesManager configMappersManager, FilterServicesManager filterServicesManager) {
        this.configMappersManager = configMappersManager;
        this.filterServicesManager = filterServicesManager;
    }

    protected List<?> apply(List<?> generatedValues, List<Filter> filters) {
        return filterServicesManager.getByType(getType().getBaseType()).apply(generatedValues, filters);
    }

}
