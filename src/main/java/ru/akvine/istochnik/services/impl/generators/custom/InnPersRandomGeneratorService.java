package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.generators.custom.inn.personal.InnPersGenerator;
import ru.akvine.istochnik.services.generators.custom.inn.personal.InnPersGeneratorConfig;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

import java.util.List;

@Service
public class InnPersRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final InnPersGenerator innPersGenerator;

    @Autowired
    protected InnPersRandomGeneratorService(ConfigMapperServicesProvider configMappersProvider,
                                            InnPersGenerator innPersGenerator,
                                            ConverterConvertersProvider converterConvertersProvider) {
        super(configMappersProvider, converterConvertersProvider);
        this.innPersGenerator = innPersGenerator;
    }

    @Override
    public List<?> generate(Config config, List<Converter> converters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper = configMappersProvider
                .configMappers()
                .get(getType().getName());
        InnPersGeneratorConfig mappedConfig = (InnPersGeneratorConfig) configMapper.map(config);
        return apply((List<?>) innPersGenerator.generate(mappedConfig), converters);
    }

    @Override
    public CustomType getType() {
        return CustomType.INN_PERSONAL;
    }
}
