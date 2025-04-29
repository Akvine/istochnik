package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.filters.FilterServicesProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.generators.custom.uuid.UuidGeneratorService;

import java.util.List;

@Service
public class UuidRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final UuidGeneratorService uuidGeneratorService;

    protected UuidRandomGeneratorService(ConfigMapperServicesProvider configMappersProvider,
                                         UuidGeneratorService uuidGeneratorService,
                                         FilterServicesProvider filterServicesProvider) {
        super(configMappersProvider, filterServicesProvider);
        this.uuidGeneratorService = uuidGeneratorService;
    }

    @Override
    public List<?> generate(Config config, List<Filter> filters) {
        return apply(uuidGeneratorService.generate(config.getSize()), filters);
    }

    @Override
    public CustomType getType() {
        return CustomType.UUID;
    }
}
