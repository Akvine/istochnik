package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.generators.custom.requisite.OkvedRandomGenerator;

@Service
public class OkvedRandomGeneratorService extends AbstractRequisiteGeneratorService {
    protected OkvedRandomGeneratorService(
            ConfigMapperServicesProvider configMappersProvider,
            ConverterConvertersProvider converterConvertersProvider,
            OkvedRandomGenerator okvedRandomGenerator) {
        super(configMappersProvider, converterConvertersProvider, okvedRandomGenerator);
    }

    @Override
    public CustomType getType() {
        return CustomType.OKVED;
    }
}
