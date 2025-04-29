package ru.akvine.istochnik.services.impl.generators.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.filters.FilterServicesProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.generators.base.number.integer.IntegerGeneratorService;
import ru.akvine.istochnik.services.generators.base.number.integer.configs.IntegerGeneratorConfig;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

import java.util.List;

@Service
public class IntegerRandomGeneratorService extends AbstractBaseTypeGeneratorService {
    private final IntegerGeneratorService integerGeneratorService;

    @Autowired
    protected IntegerRandomGeneratorService(ConfigMapperServicesProvider configMappersProvider,
                                            IntegerGeneratorService integerGeneratorService,
                                            FilterServicesProvider filterServicesProvider) {
        super(configMappersProvider, filterServicesProvider);
        this.integerGeneratorService = integerGeneratorService;
    }

    @Override
    public List<?> generate(Config config, List<Filter> filters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper = configMappersProvider
                .configMappers()
                .get(getType().getValue());
        IntegerGeneratorConfig mappedConfig = (IntegerGeneratorConfig) configMapper.map(config);
        return apply(integerGeneratorService.generate(mappedConfig), filters);
    }

    @Override
    public BaseType getType() {
        return BaseType.INTEGER;
    }
}
