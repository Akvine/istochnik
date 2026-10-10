package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.generators.custom.requisite.KbkRandomGenerator;

@Service
public class KbkRandomGeneratorService extends AbstractRequisiteGeneratorService {
    protected KbkRandomGeneratorService(
            ConfigMapperServicesProvider configMappersProvider,
            ConverterConvertersProvider converterConvertersProvider,
            KbkRandomGenerator kbkRandomGenerator) {
        super(configMappersProvider, converterConvertersProvider, kbkRandomGenerator);
    }

    @Override
    public CustomType getType() {
        return CustomType.KBK;
    }
}
