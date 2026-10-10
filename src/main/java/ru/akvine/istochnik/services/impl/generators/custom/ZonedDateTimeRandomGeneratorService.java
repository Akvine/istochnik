package ru.akvine.istochnik.services.impl.generators.custom;

import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.generators.custom.zoneddatetime.ZonedDateTimeGeneratorService;
import ru.akvine.istochnik.services.generators.custom.zoneddatetime.configs.ZonedDateTimeGeneratorConfig;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;
import ru.akvine.istochnik.utils.ZonedDateTimeUtils;

@Service
public class ZonedDateTimeRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final ZonedDateTimeGeneratorService zonedDateTimeGeneratorService;

    protected ZonedDateTimeRandomGeneratorService(
            ConfigMapperServicesProvider configMappersProvider,
            ZonedDateTimeGeneratorService zonedDateTimeGeneratorService,
            ConverterConvertersProvider converterConvertersProvider) {
        super(configMappersProvider, converterConvertersProvider);
        this.zonedDateTimeGeneratorService = zonedDateTimeGeneratorService;
    }

    @Override
    public List<?> generate(Config config, List<Converter> converters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper =
                configMappersProvider.configMappers().get(getType().getName());
        ZonedDateTimeGeneratorConfig mappedConfig = (ZonedDateTimeGeneratorConfig) configMapper.map(config);
        return apply(
                transformToString(zonedDateTimeGeneratorService.generate(mappedConfig)),
                converters,
                config.getRandomGenerator());
    }

    @Override
    public CustomType getType() {
        return CustomType.ZONED_DATETIME;
    }

    private List<String> transformToString(List<ZonedDateTime> values) {
        return values.stream()
                .map(zonedDateTime -> {
                    if (zonedDateTime == null) {
                        return null;
                    } else {
                        return ZonedDateTimeUtils.format(zonedDateTime);
                    }
                })
                .toList();
    }
}
