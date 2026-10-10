package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.generators.custom.requisite.KppRandomGenerator;

@Service
public class KppRandomGeneratorService extends AbstractRequisiteGeneratorService {
    protected KppRandomGeneratorService(
            ConfigMapperServicesProvider configMappersProvider,
            ConverterConvertersProvider converterConvertersProvider,
            KppRandomGenerator kppRandomGenerator) {
        super(configMappersProvider, converterConvertersProvider, kppRandomGenerator);
    }

    @Override
    public CustomType getType() {
        return CustomType.KPP;
    }
}
