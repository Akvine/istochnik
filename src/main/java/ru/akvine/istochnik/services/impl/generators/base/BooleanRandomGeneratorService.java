package ru.akvine.istochnik.services.impl.generators.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.filters.FilterServicesProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.generators.bool.BooleanGeneratorService;
import ru.akvine.istochnik.services.generators.bool.configs.BooleanGeneratorConfig;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

import java.util.List;

@Service
public class BooleanRandomGeneratorService extends AbstractBaseTypeGeneratorService {
    private final BooleanGeneratorService booleanGeneratorService;

    @Autowired
    protected BooleanRandomGeneratorService(ConfigMapperServicesProvider configMappersProvider,
                                            BooleanGeneratorService booleanGeneratorService,
                                            FilterServicesProvider filterServicesProvider) {
        super(configMappersProvider, filterServicesProvider);
        this.booleanGeneratorService = booleanGeneratorService;
    }

    @Override
    public List<?> generate(Config config, List<Filter> filters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper = configMappersProvider
                .configMappers()
                .get(getType().getValue());
        BooleanGeneratorConfig mappedConfig = (BooleanGeneratorConfig) configMapper.map(config);
        return apply(booleanGeneratorService.generate(mappedConfig), filters);
    }

    @Override
    public BaseType getType() {
        return BaseType.BOOLEAN;
    }
}
