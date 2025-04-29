package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.filters.FilterServicesProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.generators.custom.inn.org.InnOrgGenerator;
import ru.akvine.istochnik.services.generators.custom.inn.org.InnOrgGeneratorConfig;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

import java.util.List;

@Service
public class InnOrgRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final InnOrgGenerator innOrgGenerator;

    protected InnOrgRandomGeneratorService(ConfigMapperServicesProvider configMappersProvider,
                                           InnOrgGenerator innOrgGenerator,
                                           FilterServicesProvider filterServicesProvider) {
        super(configMappersProvider, filterServicesProvider);
        this.innOrgGenerator = innOrgGenerator;
    }

    @Override
    public List<?> generate(Config config, List<Filter> filters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper = configMappersProvider
                .configMappers()
                .get(getType().getName());
        InnOrgGeneratorConfig mappedConfig = (InnOrgGeneratorConfig) configMapper.map(config);
        return apply((List<?>) innOrgGenerator.generate(mappedConfig), filters);
    }

    @Override
    public CustomType getType() {
        return CustomType.INN_ORG;
    }
}
