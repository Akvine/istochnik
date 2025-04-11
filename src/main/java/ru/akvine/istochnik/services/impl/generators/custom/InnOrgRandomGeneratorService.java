package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.managers.ConfigMapperServicesManager;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.generators.inn.org.InnOrgGenerator;
import ru.akvine.istochnik.services.generators.inn.org.InnOrgGeneratorConfig;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

import java.util.List;

@Service
public class InnOrgRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final InnOrgGenerator innOrgGenerator;

    protected InnOrgRandomGeneratorService(ConfigMapperServicesManager configMappersManager,
                                           InnOrgGenerator innOrgGenerator) {
        super(configMappersManager);
        this.innOrgGenerator = innOrgGenerator;
    }

    @Override
    public List<?> generate(Config config, List<Filter> filters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper = configMappersManager
                .configMappers()
                .get(getType().getName());
        InnOrgGeneratorConfig mappedConfig = (InnOrgGeneratorConfig) configMapper.map(config);
        return (List<?>) innOrgGenerator.generate(mappedConfig);
    }

    @Override
    public CustomType getType() {
        return CustomType.INN_ORG;
    }
}
