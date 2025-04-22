package ru.akvine.istochnik.services.impl.generators.base;

import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.filters.FilterServicesProvider;
import ru.akvine.istochnik.services.BaseTypeGeneratorService;
import ru.akvine.istochnik.services.dto.Filter;

import java.util.List;

public abstract class AbstractBaseTypeGeneratorService implements BaseTypeGeneratorService {
    protected final ConfigMapperServicesProvider configMappersProvider;
    protected final FilterServicesProvider filterServicesProvider;

    protected AbstractBaseTypeGeneratorService(ConfigMapperServicesProvider configMappersProvider, FilterServicesProvider filterServicesProvider) {
        this.configMappersProvider = configMappersProvider;
        this.filterServicesProvider = filterServicesProvider;
    }

    protected List<?> apply(List<?> generatedValues, List<Filter> filters) {
        return filterServicesProvider.getByType(getType()).apply(generatedValues, filters);
    }
}
