package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.managers.ConfigMapperServicesManager;
import ru.akvine.istochnik.managers.filters.FilterServicesManager;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.generators.uuid.UuidGeneratorService;

import java.util.List;

@Service
public class UuidRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final UuidGeneratorService uuidGeneratorService;

    protected UuidRandomGeneratorService(ConfigMapperServicesManager configMappersManager,
                                         UuidGeneratorService uuidGeneratorService,
                                         FilterServicesManager filterServicesManager) {
        super(configMappersManager, filterServicesManager);
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
