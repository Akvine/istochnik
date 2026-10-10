package ru.akvine.istochnik.services.generators.custom.card;

import java.util.List;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.PaymentSystem;
import ru.akvine.istochnik.services.generators.custom.requisite.AbstractRequisiteRandomGenerator;
import ru.akvine.istochnik.utils.LuhnUtils;

@Service
public class CardNumberRandomGenerator extends AbstractRequisiteRandomGenerator<CardNumberGeneratorConfig> {
    private static final int CARD_NUMBER_LENGTH = 16;

    @Override
    protected String generateValue(CardNumberGeneratorConfig config) {
        RandomGenerator randomGenerator = config.getRandomGenerator();
        List<PaymentSystem.PrefixRange> prefixRanges = config.getPaymentSystem().getPrefixRanges();
        PaymentSystem.PrefixRange prefixRange = prefixRanges.get(randomGenerator.nextInt(prefixRanges.size()));

        StringBuilder cardNumber = new StringBuilder(CARD_NUMBER_LENGTH);
        cardNumber.append(randomGenerator.nextInt(prefixRange.start(), prefixRange.end() + 1));
        while (cardNumber.length() < CARD_NUMBER_LENGTH - 1) {
            cardNumber.append(randomGenerator.nextInt(10));
        }

        if (config.isValid()) {
            cardNumber.append(LuhnUtils.calculateCheckDigit(cardNumber));
        } else {
            cardNumber.append(randomGenerator.nextInt(10));
        }

        return cardNumber.toString();
    }
}
