package ru.akvine.istochnik.services.impl.generators.custom;

import java.util.List;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.generators.custom.requisite.AbstractRequisiteRandomGenerator;
import ru.akvine.istochnik.services.generators.custom.requisite.RequisiteGeneratorConfig;

/**
 * Общая генерация реквизитов, которым достаточно параметров notNull, unique и valid:
 * наследники отличаются только типом и генератором
 */
public abstract class AbstractRequisiteGeneratorService extends AbstractCustomTypeGeneratorService {
    private final AbstractRequisiteRandomGenerator<RequisiteGeneratorConfig> requisiteRandomGenerator;

    protected AbstractRequisiteGeneratorService(
            ConfigMapperServicesProvider configMappersProvider,
            ConverterConvertersProvider converterConvertersProvider,
            AbstractRequisiteRandomGenerator<RequisiteGeneratorConfig> requisiteRandomGenerator) {
        super(configMappersProvider, converterConvertersProvider);
        this.requisiteRandomGenerator = requisiteRandomGenerator;
    }

    @Override
    public List<?> generate(Config config, List<Converter> converters) {
        RequisiteGeneratorConfig mappedConfig = new RequisiteGeneratorConfig(
                config.getSize(), config.isNotNull(), config.isUnique(), config.isValid(), config.getRandomGenerator());
        return apply(requisiteRandomGenerator.generate(mappedConfig), converters, config.getRandomGenerator());
    }
}
