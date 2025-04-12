package ru.akvine.istochnik.services.impl.generators.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.managers.ConfigMapperServicesManager;
import ru.akvine.istochnik.managers.filters.FilterServicesManager;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Filter;

import java.util.List;

@Service
public class StringRandomGeneratorService extends AbstractBaseTypeGeneratorService {

    @Autowired
    protected StringRandomGeneratorService(ConfigMapperServicesManager configMappersManager,
                                           FilterServicesManager filterServicesManager) {
        super(configMappersManager, filterServicesManager);
    }

    @Override
    public List<?> generate(Config config, List<Filter> filters) {
        return List.of();
    }

    @Override
    public BaseType getType() {
        return BaseType.STRING;
    }
}
