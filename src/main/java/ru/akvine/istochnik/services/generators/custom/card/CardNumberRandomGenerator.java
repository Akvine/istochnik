package ru.akvine.istochnik.services.generators.custom.card;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.PaymentSystem;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;
import ru.akvine.istochnik.utils.LuhnUtils;

@Service
public class CardNumberRandomGenerator extends AbstractRandomGenerator<String, CardNumberGeneratorConfig> {
    private static final int CARD_NUMBER_LENGTH = 16;

    @Override
    public Collection<String> generate(CardNumberGeneratorConfig config) {
        int size = config.getSize();
        RandomGenerator randomGenerator = config.getRandomGenerator();

        List<String> generatedValues = new ArrayList<>(size);
        Set<String> uniqueValues = new HashSet<>();
        boolean nullGenerated = false;

        int iteration = 0;
        while (generatedValues.size() != size) {
            checkGenerationCountAttempts(iteration, size);
            iteration++;

            if (!config.isNotNull() && randomGenerator.nextBoolean()) {
                if (config.isUnique() && nullGenerated) {
                    continue;
                }

                nullGenerated = true;
                generatedValues.add(null);
                continue;
            }

            String cardNumber = generateCardNumber(config);
            if (config.isUnique() && !uniqueValues.add(cardNumber)) {
                continue;
            }

            generatedValues.add(cardNumber);
        }

        return generatedValues;
    }

    private String generateCardNumber(CardNumberGeneratorConfig config) {
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
