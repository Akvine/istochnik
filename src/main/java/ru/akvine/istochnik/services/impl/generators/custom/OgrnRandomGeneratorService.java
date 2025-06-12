package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.generators.custom.ogrn.OgrnGeneratorConfig;
import ru.akvine.istochnik.services.generators.custom.ogrn.OgrnRandomGenerator;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

import java.util.List;

@Service
public class OgrnRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final OgrnRandomGenerator ogrnRandomGenerator;

    protected OgrnRandomGeneratorService(ConfigMapperServicesProvider configMappersProvider,
                                         OgrnRandomGenerator ogrnRandomGenerator,
                                         ConverterConvertersProvider converterConvertersProvider) {
        super(configMappersProvider, converterConvertersProvider);
        this.ogrnRandomGenerator = ogrnRandomGenerator;
    }

    @Override
    public List<?> generate(Config config, List<Converter> converters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper = configMappersProvider
                .configMappers()
                .get(getType().getName());
        OgrnGeneratorConfig mappedConfig = (OgrnGeneratorConfig) configMapper.map(config);
        return apply((List<?>) ogrnRandomGenerator.generate(mappedConfig), converters);
    }

    @Override
    public CustomType getType() {
        return CustomType.OGRN;
    }
}
