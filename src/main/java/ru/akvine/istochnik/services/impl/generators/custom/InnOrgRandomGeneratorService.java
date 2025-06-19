package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.generators.custom.inn.org.InnOrgGenerator;
import ru.akvine.istochnik.services.generators.custom.inn.org.InnOrgGeneratorConfig;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

import java.util.List;

@Service
public class InnOrgRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final InnOrgGenerator innOrgGenerator;

    protected InnOrgRandomGeneratorService(ConfigMapperServicesProvider configMappersProvider,
                                           InnOrgGenerator innOrgGenerator,
                                           ConverterConvertersProvider converterConvertersProvider) {
        super(configMappersProvider, converterConvertersProvider);
        this.innOrgGenerator = innOrgGenerator;
    }

    @Override
    public List<?> generate(Config config, List<Converter> converters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper = configMappersProvider
                .configMappers()
                .get(getType().getName());
        InnOrgGeneratorConfig mappedConfig = (InnOrgGeneratorConfig) configMapper.map(config);
        return apply((List<?>) innOrgGenerator.generate(mappedConfig), converters, config.getRandomGenerator());
    }

    @Override
    public CustomType getType() {
        return CustomType.INN_ORG;
    }
}
