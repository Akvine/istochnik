package ru.akvine.istochnik.services.generators.custom.requisite;

import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;

@Service
public class OktmoRandomGenerator extends AbstractTerritoryCodeRandomGenerator {
    private static final String NO_SETTLEMENT = "000";

    // Формат ОКТМО муниципального образования (8 цифр): субъект РФ, муниципальный район или городской округ,
    // поселение. Контрольное число в сам код не входит
    @Override
    protected String generateValue(RequisiteGeneratorConfig config) {
        RandomGenerator randomGenerator = config.getRandomGenerator();

        StringBuilder oktmo = new StringBuilder();
        appendRegion(oktmo, randomGenerator);

        if (randomGenerator.nextBoolean()) {
            // Городской округ на поселения не делится
            appendNumber(oktmo, 701, 749, 3, randomGenerator);
            oktmo.append(NO_SETTLEMENT);
            return oktmo.toString();
        }

        appendNumber(oktmo, 601, 659, 3, randomGenerator);
        switch (randomGenerator.nextInt(3)) {
            case 0 -> oktmo.append(NO_SETTLEMENT);
                // Городские поселения
            case 1 -> appendNumber(oktmo, 101, 199, 3, randomGenerator);
                // Сельские поселения
            default -> appendNumber(oktmo, 401, 499, 3, randomGenerator);
        }

        return oktmo.toString();
    }
}
