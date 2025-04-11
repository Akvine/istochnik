package ru.akvine.istochnik.services.impl.generators.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.managers.ConfigMapperServicesManager;
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
    protected BooleanRandomGeneratorService(ConfigMapperServicesManager configMappersManager,
                                            BooleanGeneratorService booleanGeneratorService) {
        super(configMappersManager);
        this.booleanGeneratorService = booleanGeneratorService;
    }

    @Override
    public List<?> generate(Config config, List<Filter> filters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper = configMappersManager
                .configMappers()
                .get(getType().getValue());
        BooleanGeneratorConfig mappedConfig = (BooleanGeneratorConfig) configMapper.map(config);
        return booleanGeneratorService.generate(mappedConfig);
    }

    @Override
    public BaseType getType() {
        return BaseType.BOOLEAN;
    }
}
