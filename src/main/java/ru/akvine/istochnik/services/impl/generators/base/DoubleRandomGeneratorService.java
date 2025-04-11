package ru.akvine.istochnik.services.impl.generators.base;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.managers.ConfigMapperServicesManager;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.generators.number.doubles.DoubleGeneratorService;
import ru.akvine.istochnik.services.generators.number.doubles.configs.DoubleGeneratorConfig;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

import java.util.List;

@Service
public class DoubleRandomGeneratorService extends AbstractBaseTypeGeneratorService {
    private final DoubleGeneratorService doubleGeneratorService;

    protected DoubleRandomGeneratorService(ConfigMapperServicesManager configMappersManager,
                                           DoubleGeneratorService doubleGeneratorService) {
        super(configMappersManager);
        this.doubleGeneratorService = doubleGeneratorService;
    }

    @Override
    public List<?> generate(Config config, List<Filter> filters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper = configMappersManager
                .configMappers()
                .get(getType().getValue());
        DoubleGeneratorConfig mappedConfig = (DoubleGeneratorConfig) configMapper.map(config);
        return doubleGeneratorService.generate(mappedConfig, filters);
    }

    @Override
    public BaseType getType() {
        return BaseType.DOUBLE;
    }
}
