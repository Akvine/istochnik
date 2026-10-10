package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.generators.custom.requisite.PtsRandomGenerator;

@Service
public class PtsRandomGeneratorService extends AbstractRequisiteGeneratorService {
    protected PtsRandomGeneratorService(
            ConfigMapperServicesProvider configMappersProvider,
            ConverterConvertersProvider converterConvertersProvider,
            PtsRandomGenerator ptsRandomGenerator) {
        super(configMappersProvider, converterConvertersProvider, ptsRandomGenerator);
    }

    @Override
    public CustomType getType() {
        return CustomType.PTS;
    }
}
