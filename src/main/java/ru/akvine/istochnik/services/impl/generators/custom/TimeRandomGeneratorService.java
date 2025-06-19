package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.generators.custom.time.TimeGeneratorService;
import ru.akvine.istochnik.services.generators.custom.time.configs.TimeGeneratorConfig;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

import java.time.LocalTime;
import java.util.List;

@Service
public class TimeRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final TimeGeneratorService timeGeneratorService;

    protected TimeRandomGeneratorService(ConfigMapperServicesProvider configMappersProvider,
                                         TimeGeneratorService timeGeneratorService,
                                         ConverterConvertersProvider converterConvertersProvider) {
        super(configMappersProvider, converterConvertersProvider);
        this.timeGeneratorService = timeGeneratorService;
    }

    @Override
    public List<?> generate(Config config, List<Converter> converters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper = configMappersProvider
                .configMappers()
                .get(getType().getName());
        TimeGeneratorConfig mappedConfig = (TimeGeneratorConfig) configMapper.map(config);
        return apply(transformToString(timeGeneratorService.generate(mappedConfig)), converters, config.getRandomGenerator());
    }

    @Override
    public CustomType getType() {
        return CustomType.TIME;
    }

    private List<String> transformToString(List<LocalTime> values) {
        return values.stream().map(time -> {
            if (time == null) {
                return null;
            } else {
                return time.toString();
            }
        }).toList();
    }
}
