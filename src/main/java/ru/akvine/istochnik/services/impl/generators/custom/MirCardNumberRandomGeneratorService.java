package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.PaymentSystem;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.generators.custom.card.CardNumberRandomGenerator;

@Service
public class MirCardNumberRandomGeneratorService extends AbstractCardNumberGeneratorService {
    protected MirCardNumberRandomGeneratorService(
            ConfigMapperServicesProvider configMappersProvider,
            ConverterConvertersProvider converterConvertersProvider,
            CardNumberRandomGenerator cardNumberRandomGenerator) {
        super(configMappersProvider, converterConvertersProvider, cardNumberRandomGenerator);
    }

    @Override
    public CustomType getType() {
        return CustomType.CARD_MIR;
    }

    @Override
    protected PaymentSystem getPaymentSystem() {
        return PaymentSystem.MIR;
    }
}
