package ru.akvine.istochnik.services.impl.generators.base;

import org.springframework.util.CollectionUtils;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.BaseTypeGeneratorService;
import ru.akvine.istochnik.services.dto.Converter;

import java.util.List;
import java.util.random.RandomGenerator;

public abstract class AbstractBaseTypeGeneratorService implements BaseTypeGeneratorService {
    protected final ConfigMapperServicesProvider configMappersProvider;
    protected final ConverterConvertersProvider converterConvertersProvider;

    protected AbstractBaseTypeGeneratorService(ConfigMapperServicesProvider configMappersProvider, ConverterConvertersProvider converterConvertersProvider) {
        this.configMappersProvider = configMappersProvider;
        this.converterConvertersProvider = converterConvertersProvider;
    }

    protected List<?> apply(List<?> generatedValues, List<Converter> converters, RandomGenerator randomGenerator) {
        if (CollectionUtils.isEmpty(converters)) {
            return generatedValues;
        }
        return converterConvertersProvider.getByType(getType()).apply(generatedValues, converters, randomGenerator);
    }
}
