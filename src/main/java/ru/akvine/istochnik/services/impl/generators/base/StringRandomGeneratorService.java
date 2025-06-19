package ru.akvine.istochnik.services.impl.generators.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;

import java.util.List;

@Service
public class StringRandomGeneratorService extends AbstractBaseTypeGeneratorService {

    @Autowired
    protected StringRandomGeneratorService(ConfigMapperServicesProvider configMappersProvider,
                                           ConverterConvertersProvider converterConvertersProvider) {
        super(configMappersProvider, converterConvertersProvider);
    }

    @Override
    // TODO: сделать рандомную генерацию строк
    public List<?> generate(Config config, List<Converter> converters) {
        return List.of();
    }

    @Override
    public BaseType getType() {
        return BaseType.STRING;
    }
}
