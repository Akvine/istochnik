package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.filters.FilterServicesProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.generators.snils.SnilsRandomGenerator;
import ru.akvine.istochnik.services.generators.snils.configs.SnilsGeneratorConfig;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

import java.util.List;

@Service
public class SnilsRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final SnilsRandomGenerator snilsRandomGenerator;

    @Autowired
    protected SnilsRandomGeneratorService(ConfigMapperServicesProvider configMappersProvider,
                                          SnilsRandomGenerator snilsRandomGenerator,
                                          FilterServicesProvider filterServicesProvider) {
        super(configMappersProvider, filterServicesProvider);
        this.snilsRandomGenerator = snilsRandomGenerator;
    }

    @Override
    public List<?> generate(Config config, List<Filter> filters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper = configMappersProvider
                .configMappers()
                .get(getType().getName());
        SnilsGeneratorConfig mappedConfig = (SnilsGeneratorConfig) configMapper.map(config);
        return apply((List<?>) snilsRandomGenerator.generate(mappedConfig), filters);
    }

    @Override
    public CustomType getType() {
        return CustomType.SNILS;
    }
}
