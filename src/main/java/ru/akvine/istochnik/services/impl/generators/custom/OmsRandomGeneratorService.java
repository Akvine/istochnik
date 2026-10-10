package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.generators.custom.requisite.OmsRandomGenerator;

@Service
public class OmsRandomGeneratorService extends AbstractRequisiteGeneratorService {
    protected OmsRandomGeneratorService(
            ConfigMapperServicesProvider configMappersProvider,
            ConverterConvertersProvider converterConvertersProvider,
            OmsRandomGenerator omsRandomGenerator) {
        super(configMappersProvider, converterConvertersProvider, omsRandomGenerator);
    }

    @Override
    public CustomType getType() {
        return CustomType.OMS;
    }
}
