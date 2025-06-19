package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.generators.custom.uuid.UuidGeneratorService;

import java.util.List;

@Service
public class UuidRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final UuidGeneratorService uuidGeneratorService;

    protected UuidRandomGeneratorService(ConfigMapperServicesProvider configMappersProvider,
                                         UuidGeneratorService uuidGeneratorService,
                                         ConverterConvertersProvider converterConvertersProvider) {
        super(configMappersProvider, converterConvertersProvider);
        this.uuidGeneratorService = uuidGeneratorService;
    }

    @Override
    public List<?> generate(Config config, List<Converter> converters) {
        return apply(uuidGeneratorService.generate(config.getSize()), converters, config.getRandomGenerator());
    }

    @Override
    public CustomType getType() {
        return CustomType.UUID;
    }
}
