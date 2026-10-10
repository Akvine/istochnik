package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.generators.custom.requisite.OkatoRandomGenerator;

@Service
public class OkatoRandomGeneratorService extends AbstractRequisiteGeneratorService {
    protected OkatoRandomGeneratorService(
            ConfigMapperServicesProvider configMappersProvider,
            ConverterConvertersProvider converterConvertersProvider,
            OkatoRandomGenerator okatoRandomGenerator) {
        super(configMappersProvider, converterConvertersProvider, okatoRandomGenerator);
    }

    @Override
    public CustomType getType() {
        return CustomType.OKATO;
    }
}
