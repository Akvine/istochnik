package ru.akvine.istochnik.services.impl.generators.base;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.filters.FilterServicesProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.generators.number.doubles.DoubleGeneratorService;
import ru.akvine.istochnik.services.generators.number.doubles.configs.DoubleGeneratorConfig;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

import java.util.List;

@Service
public class DoubleRandomGeneratorService extends AbstractBaseTypeGeneratorService {
    private final DoubleGeneratorService doubleGeneratorService;

    protected DoubleRandomGeneratorService(ConfigMapperServicesProvider configMappersProvider,
                                           DoubleGeneratorService doubleGeneratorService,
                                           FilterServicesProvider filterServicesProvider) {
        super(configMappersProvider, filterServicesProvider);
        this.doubleGeneratorService = doubleGeneratorService;
    }

    @Override
    public List<?> generate(Config config, List<Filter> filters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper = configMappersProvider
                .configMappers()
                .get(getType().getValue());
        DoubleGeneratorConfig mappedConfig = (DoubleGeneratorConfig) configMapper.map(config);
        return apply(doubleGeneratorService.generate(mappedConfig), filters);
    }

    @Override
    public BaseType getType() {
        return BaseType.DOUBLE;
    }
}
