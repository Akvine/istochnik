package ru.akvine.istochnik.services.impl.generators.custom;

import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.filters.FilterServicesProvider;
import ru.akvine.istochnik.services.CustomTypeGeneratorService;
import ru.akvine.istochnik.services.dto.Filter;

import java.util.List;

public abstract class AbstractCustomTypeGeneratorService implements CustomTypeGeneratorService {
    protected final ConfigMapperServicesProvider configMappersProvider;
    protected final FilterServicesProvider filterServicesProvider;

    protected AbstractCustomTypeGeneratorService(ConfigMapperServicesProvider configMappersProvider, FilterServicesProvider filterServicesProvider) {
        this.configMappersProvider = configMappersProvider;
        this.filterServicesProvider = filterServicesProvider;
    }

    protected List<?> apply(List<?> generatedValues, List<Filter> filters) {
        return filterServicesProvider.getByType(getType().getBaseType()).apply(generatedValues, filters);
    }

}
