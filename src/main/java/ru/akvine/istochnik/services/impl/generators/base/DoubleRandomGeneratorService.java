package ru.akvine.istochnik.services.impl.generators.base;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.generators.base.number.doubles.DoubleGeneratorService;
import ru.akvine.istochnik.services.generators.base.number.doubles.configs.DoubleGeneratorConfig;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

import java.util.List;

@Service
public class DoubleRandomGeneratorService extends AbstractBaseTypeGeneratorService {
    private final DoubleGeneratorService doubleGeneratorService;

    protected DoubleRandomGeneratorService(ConfigMapperServicesProvider configMappersProvider,
                                           DoubleGeneratorService doubleGeneratorService,
                                           ConverterConvertersProvider converterConvertersProvider) {
        super(configMappersProvider, converterConvertersProvider);
        this.doubleGeneratorService = doubleGeneratorService;
    }

    @Override
    public List<?> generate(Config config, List<Converter> converters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper = configMappersProvider
                .configMappers()
                .get(getType().getValue());
        DoubleGeneratorConfig mappedConfig = (DoubleGeneratorConfig) configMapper.map(config);
        return apply(doubleGeneratorService.generate(mappedConfig), converters);
    }

    @Override
    public BaseType getType() {
        return BaseType.DOUBLE;
    }
}
