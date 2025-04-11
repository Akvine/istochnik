package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.managers.ConfigMapperServicesManager;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.generators.date.DateGeneratorService;
import ru.akvine.istochnik.services.generators.date.configs.DateGeneratorConfig;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

import java.util.List;

@Service
public class DateRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final DateGeneratorService dateGeneratorService;

    @Autowired
    protected DateRandomGeneratorService(ConfigMapperServicesManager configMappersManager, DateGeneratorService dateGeneratorService) {
        super(configMappersManager);
        this.dateGeneratorService = dateGeneratorService;
    }

    @Override
    public List<?> generate(Config config, List<Filter> filters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper = configMappersManager
                .configMappers()
                .get(getType().getName());
        DateGeneratorConfig mappedConfig = (DateGeneratorConfig) configMapper.map(config);
        return dateGeneratorService.generate(mappedConfig);
    }

    @Override
    public CustomType getType() {
        return CustomType.DATE;
    }
}
