package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.generators.custom.requisite.StsRandomGenerator;

@Service
public class StsRandomGeneratorService extends AbstractRequisiteGeneratorService {
    protected StsRandomGeneratorService(
            ConfigMapperServicesProvider configMappersProvider,
            ConverterConvertersProvider converterConvertersProvider,
            StsRandomGenerator stsRandomGenerator) {
        super(configMappersProvider, converterConvertersProvider, stsRandomGenerator);
    }

    @Override
    public CustomType getType() {
        return CustomType.STS;
    }
}
