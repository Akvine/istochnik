package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.generators.custom.date.DateGeneratorService;
import ru.akvine.istochnik.services.generators.custom.date.configs.DateGeneratorConfig;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

import java.time.LocalDate;
import java.util.List;

@Service
public class DateRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final DateGeneratorService dateGeneratorService;

    @Autowired
    protected DateRandomGeneratorService(ConfigMapperServicesProvider configMappersProvider,
                                         DateGeneratorService dateGeneratorService,
                                         ConverterConvertersProvider converterConvertersProvider) {
        super(configMappersProvider, converterConvertersProvider);
        this.dateGeneratorService = dateGeneratorService;
    }

    @Override
    public List<?> generate(Config config, List<Converter> converters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper = configMappersProvider
                .configMappers()
                .get(getType().getName());
        DateGeneratorConfig mappedConfig = (DateGeneratorConfig) configMapper.map(config);
        return apply(transformToString(dateGeneratorService.generate(mappedConfig)), converters);
    }

    @Override
    public CustomType getType() {
        return CustomType.DATE;
    }

    private List<String> transformToString(List<LocalDate> values) {
        return values.stream().map(date -> {
            if (date == null) {
                return null;
            } else {
                return date.toString();
            }
        }).toList();
    }
}
