package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.managers.ConfigMapperServicesManager;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.generators.time.TimeGeneratorService;
import ru.akvine.istochnik.services.generators.time.configs.TimeGeneratorConfig;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

import java.util.List;

@Service
public class TimeRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final TimeGeneratorService timeGeneratorService;

    protected TimeRandomGeneratorService(ConfigMapperServicesManager configMappersManager, TimeGeneratorService timeGeneratorService) {
        super(configMappersManager);
        this.timeGeneratorService = timeGeneratorService;
    }

    @Override
    public List<?> generate(Config config, List<Filter> filters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper = configMappersManager
                .configMappers()
                .get(getType().getName());
        TimeGeneratorConfig mappedConfig = (TimeGeneratorConfig) configMapper.map(config);
        return timeGeneratorService.generate(mappedConfig);
    }

    @Override
    public CustomType getType() {
        return CustomType.TIME;
    }
}
