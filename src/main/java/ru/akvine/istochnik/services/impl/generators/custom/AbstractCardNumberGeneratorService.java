package ru.akvine.istochnik.services.impl.generators.custom;

import java.util.List;
import ru.akvine.istochnik.enums.PaymentSystem;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.generators.custom.card.CardNumberGeneratorConfig;
import ru.akvine.istochnik.services.generators.custom.card.CardNumberRandomGenerator;

/**
 * Общая генерация номеров карт: наследники отличаются только платёжной системой
 */
public abstract class AbstractCardNumberGeneratorService extends AbstractCustomTypeGeneratorService {
    private final CardNumberRandomGenerator cardNumberRandomGenerator;

    protected AbstractCardNumberGeneratorService(
            ConfigMapperServicesProvider configMappersProvider,
            ConverterConvertersProvider converterConvertersProvider,
            CardNumberRandomGenerator cardNumberRandomGenerator) {
        super(configMappersProvider, converterConvertersProvider);
        this.cardNumberRandomGenerator = cardNumberRandomGenerator;
    }

    @Override
    public List<?> generate(Config config, List<Converter> converters) {
        CardNumberGeneratorConfig mappedConfig = new CardNumberGeneratorConfig(
                config.getSize(),
                config.isNotNull(),
                config.isUnique(),
                config.isValid(),
                getPaymentSystem(),
                config.getRandomGenerator());
        return apply(
                (List<?>) cardNumberRandomGenerator.generate(mappedConfig), converters, config.getRandomGenerator());
    }

    protected abstract PaymentSystem getPaymentSystem();
}
