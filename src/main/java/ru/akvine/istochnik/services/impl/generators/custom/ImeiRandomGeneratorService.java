package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.generators.custom.imei.ImeiGeneratorConfig;
import ru.akvine.istochnik.services.generators.custom.imei.ImeiRandomGenerator;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

import java.util.List;

@Service
public class ImeiRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final ImeiRandomGenerator imeiRandomGenerator;

    protected ImeiRandomGeneratorService(ConfigMapperServicesProvider configMappersProvider,
                                         ConverterConvertersProvider converterConvertersProvider, ImeiRandomGenerator imeiRandomGenerator) {
        super(configMappersProvider, converterConvertersProvider);
        this.imeiRandomGenerator = imeiRandomGenerator;
    }

    @Override
    public List<?> generate(Config config, List<Converter> converters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper = configMappersProvider
                .configMappers()
                .get(getType().getName());
        ImeiGeneratorConfig mappedConfig = (ImeiGeneratorConfig) configMapper.map(config);
        return apply((List<?>) imeiRandomGenerator.generate(mappedConfig), converters, config.getRandomGenerator());
    }

    @Override
    public CustomType getType() {
        return CustomType.IMEI;
    }
}
