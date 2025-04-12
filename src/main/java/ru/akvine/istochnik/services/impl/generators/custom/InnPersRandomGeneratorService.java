package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.managers.ConfigMapperServicesManager;
import ru.akvine.istochnik.managers.filters.FilterServicesManager;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.generators.inn.personal.InnPersGenerator;
import ru.akvine.istochnik.services.generators.inn.personal.InnPersGeneratorConfig;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

import java.util.List;

@Service
public class InnPersRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final InnPersGenerator innPersGenerator;

    @Autowired
    protected InnPersRandomGeneratorService(ConfigMapperServicesManager configMappersManager,
                                            InnPersGenerator innPersGenerator,
                                            FilterServicesManager filterServicesManager) {
        super(configMappersManager, filterServicesManager);
        this.innPersGenerator = innPersGenerator;
    }

    @Override
    public List<?> generate(Config config, List<Filter> filters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper = configMappersManager
                .configMappers()
                .get(getType().getName());
        InnPersGeneratorConfig mappedConfig = (InnPersGeneratorConfig) configMapper.map(config);
        return apply((List<?>) innPersGenerator.generate(mappedConfig), filters);
    }

    @Override
    public CustomType getType() {
        return CustomType.INN_PERSONAL;
    }
}
