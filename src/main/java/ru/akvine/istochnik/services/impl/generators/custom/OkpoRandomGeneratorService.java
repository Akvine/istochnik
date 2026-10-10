package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.generators.custom.requisite.OkpoRandomGenerator;

@Service
public class OkpoRandomGeneratorService extends AbstractRequisiteGeneratorService {
    protected OkpoRandomGeneratorService(
            ConfigMapperServicesProvider configMappersProvider,
            ConverterConvertersProvider converterConvertersProvider,
            OkpoRandomGenerator okpoRandomGenerator) {
        super(configMappersProvider, converterConvertersProvider, okpoRandomGenerator);
    }

    @Override
    public CustomType getType() {
        return CustomType.OKPO;
    }
}
