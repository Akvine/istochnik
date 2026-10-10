package ru.akvine.istochnik.services.impl.generators.custom;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.generators.custom.requisite.BankAccountGeneratorConfig;
import ru.akvine.istochnik.services.generators.custom.requisite.BankAccountRandomGenerator;

@Service
public class BankAccountRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final BankAccountRandomGenerator bankAccountRandomGenerator;

    protected BankAccountRandomGeneratorService(
            ConfigMapperServicesProvider configMappersProvider,
            ConverterConvertersProvider converterConvertersProvider,
            BankAccountRandomGenerator bankAccountRandomGenerator) {
        super(configMappersProvider, converterConvertersProvider);
        this.bankAccountRandomGenerator = bankAccountRandomGenerator;
    }

    @Override
    public List<?> generate(Config config, List<Converter> converters) {
        // БИК банка, под который рассчитывается контрольный ключ, передаётся в поле start
        BankAccountGeneratorConfig mappedConfig = new BankAccountGeneratorConfig(
                config.getSize(),
                config.isNotNull(),
                config.isUnique(),
                config.isValid(),
                StringUtils.trimToNull(config.getStart()),
                config.getRandomGenerator());
        return apply(bankAccountRandomGenerator.generate(mappedConfig), converters, config.getRandomGenerator());
    }

    @Override
    public CustomType getType() {
        return CustomType.BANK_ACCOUNT;
    }
}
