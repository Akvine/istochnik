package ru.akvine.istochnik.services.impl.generators.custom;

import java.util.List;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.generators.custom.uuid.UuidGeneratorService;

@Service
public class UuidRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final UuidGeneratorService uuidGeneratorService;

    protected UuidRandomGeneratorService(
            ConfigMapperServicesProvider configMappersProvider,
            UuidGeneratorService uuidGeneratorService,
            ConverterConvertersProvider converterConvertersProvider) {
        super(configMappersProvider, converterConvertersProvider);
        this.uuidGeneratorService = uuidGeneratorService;
    }

    @Override
    public List<?> generate(Config config, List<Converter> converters) {
        ru.akvine.istochnik.services.generators.Config mappedConfig =
                new ru.akvine.istochnik.services.generators.Config(
                        config.getSize(), config.getRandomGenerator(), config.getSeed());
        return apply(uuidGeneratorService.generate(mappedConfig), converters, config.getRandomGenerator());
    }

    @Override
    public CustomType getType() {
        return CustomType.UUID;
    }
}
