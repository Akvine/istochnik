package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.managers.ConfigMapperServicesManager;
import ru.akvine.istochnik.managers.filters.FilterServicesManager;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.generators.time.TimeGeneratorService;
import ru.akvine.istochnik.services.generators.time.configs.TimeGeneratorConfig;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

import java.time.LocalTime;
import java.util.List;

@Service
public class TimeRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final TimeGeneratorService timeGeneratorService;

    protected TimeRandomGeneratorService(ConfigMapperServicesManager configMappersManager,
                                         TimeGeneratorService timeGeneratorService,
                                         FilterServicesManager filterServicesManager) {
        super(configMappersManager, filterServicesManager);
        this.timeGeneratorService = timeGeneratorService;
    }

    @Override
    public List<?> generate(Config config, List<Filter> filters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper = configMappersManager
                .configMappers()
                .get(getType().getName());
        TimeGeneratorConfig mappedConfig = (TimeGeneratorConfig) configMapper.map(config);
        return apply(transformToString(timeGeneratorService.generate(mappedConfig)), filters);
    }

    @Override
    public CustomType getType() {
        return CustomType.TIME;
    }

    private List<String> transformToString(List<LocalTime> values) {
        return values.stream().map(LocalTime::toString).toList();
    }
}
