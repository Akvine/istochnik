package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.generators.custom.requisite.OgrnipRandomGenerator;

@Service
public class OgrnipRandomGeneratorService extends AbstractRequisiteGeneratorService {
    protected OgrnipRandomGeneratorService(
            ConfigMapperServicesProvider configMappersProvider,
            ConverterConvertersProvider converterConvertersProvider,
            OgrnipRandomGenerator ogrnipRandomGenerator) {
        super(configMappersProvider, converterConvertersProvider, ogrnipRandomGenerator);
    }

    @Override
    public CustomType getType() {
        return CustomType.OGRNIP;
    }
}
