package ru.akvine.istochnik.services.impl.generators.custom;

import java.util.List;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.generators.custom.zoneid.ZoneIdGenerator;
import ru.akvine.istochnik.services.generators.custom.zoneid.ZoneIdGeneratorConfig;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

@Service
public class ZoneIdRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final ZoneIdGenerator zoneIdGenerator;

    protected ZoneIdRandomGeneratorService(
            ConfigMapperServicesProvider configMappersProvider,
            ZoneIdGenerator zoneIdGenerator,
            ConverterConvertersProvider converterConvertersProvider) {
        super(configMappersProvider, converterConvertersProvider);
        this.zoneIdGenerator = zoneIdGenerator;
    }

    @Override
    public List<?> generate(Config config, List<Converter> converters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper =
                configMappersProvider.configMappers().get(getType().getName());
        ZoneIdGeneratorConfig mappedConfig = (ZoneIdGeneratorConfig) configMapper.map(config);
        return apply(zoneIdGenerator.generate(mappedConfig), converters, config.getRandomGenerator());
    }

    @Override
    public CustomType getType() {
        return CustomType.ZONE_ID;
    }
}
