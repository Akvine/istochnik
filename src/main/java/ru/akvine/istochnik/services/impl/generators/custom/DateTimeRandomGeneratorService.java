package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.managers.ConfigMapperServicesManager;
import ru.akvine.istochnik.managers.filters.FilterServicesManager;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.generators.datetime.DateTimeGeneratorService;
import ru.akvine.istochnik.services.generators.datetime.configs.DateTimeGeneratorConfig;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DateTimeRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final DateTimeGeneratorService dateTimeGeneratorService;

    @Autowired
    protected DateTimeRandomGeneratorService(ConfigMapperServicesManager configMappersManager,
                                             DateTimeGeneratorService dateTimeGeneratorService,
                                             FilterServicesManager filterServicesManager) {
        super(configMappersManager, filterServicesManager);
        this.dateTimeGeneratorService = dateTimeGeneratorService;
    }

    @Override
    public List<?> generate(Config config, List<Filter> filters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper = configMappersManager
                .configMappers()
                .get(getType().getName());
        DateTimeGeneratorConfig mappedConfig = (DateTimeGeneratorConfig) configMapper.map(config);
        return apply(transformToString(dateTimeGeneratorService.generate(mappedConfig)), filters);
    }

    @Override
    public CustomType getType() {
        return CustomType.DATETIME;
    }

    private List<String> transformToString(List<LocalDateTime> values) {
        return values.stream().map(LocalDateTime::toString).toList();
    }
}
