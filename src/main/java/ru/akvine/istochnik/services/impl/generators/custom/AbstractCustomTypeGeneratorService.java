package ru.akvine.istochnik.services.impl.generators.custom;

import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.CustomTypeGeneratorService;
import ru.akvine.istochnik.services.dto.Converter;

import java.util.List;

public abstract class AbstractCustomTypeGeneratorService implements CustomTypeGeneratorService {
    protected final ConfigMapperServicesProvider configMappersProvider;
    protected final ConverterConvertersProvider converterConvertersProvider;

    protected AbstractCustomTypeGeneratorService(ConfigMapperServicesProvider configMappersProvider, ConverterConvertersProvider converterConvertersProvider) {
        this.configMappersProvider = configMappersProvider;
        this.converterConvertersProvider = converterConvertersProvider;
    }

    protected List<?> apply(List<?> generatedValues, List<Converter> converters) {
        return converterConvertersProvider.getByType(getType().getBaseType()).apply(generatedValues, converters);
    }

}
