package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.generators.custom.datetime.DateTimeGeneratorService;
import ru.akvine.istochnik.services.generators.custom.datetime.configs.DateTimeGeneratorConfig;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DateTimeRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final DateTimeGeneratorService dateTimeGeneratorService;

    @Autowired
    protected DateTimeRandomGeneratorService(ConfigMapperServicesProvider configMappersProvider,
                                             DateTimeGeneratorService dateTimeGeneratorService,
                                             ConverterConvertersProvider converterConvertersProvider) {
        super(configMappersProvider, converterConvertersProvider);
        this.dateTimeGeneratorService = dateTimeGeneratorService;
    }

    @Override
    public List<?> generate(Config config, List<Converter> converters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper = configMappersProvider
                .configMappers()
                .get(getType().getName());
        DateTimeGeneratorConfig mappedConfig = (DateTimeGeneratorConfig) configMapper.map(config);
        return apply(transformToString(dateTimeGeneratorService.generate(mappedConfig)),
                converters,
                config.getRandomGenerator());
    }

    @Override
    public CustomType getType() {
        return CustomType.DATETIME;
    }

    private List<String> transformToString(List<LocalDateTime> values) {
        return values.stream().map(dateTime -> {
            if (dateTime == null) {
                return null;
            } else {
                return dateTime.toString();
            }
        }).toList();
    }
}
