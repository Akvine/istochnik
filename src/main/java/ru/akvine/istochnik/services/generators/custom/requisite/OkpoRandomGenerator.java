package ru.akvine.istochnik.services.generators.custom.requisite;

import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.utils.RequisiteUtils;

@Service
public class OkpoRandomGenerator extends AbstractRequisiteRandomGenerator<RequisiteGeneratorConfig> {
    // ОКПО юридического лица: семь цифр номера и контрольная цифра
    private static final int PAYLOAD_LENGTH = 7;

    @Override
    protected String generateValue(RequisiteGeneratorConfig config) {
        RandomGenerator randomGenerator = config.getRandomGenerator();

        StringBuilder okpo = new StringBuilder();
        appendDigits(okpo, PAYLOAD_LENGTH, randomGenerator);

        if (config.isValid()) {
            okpo.append(RequisiteUtils.calculateOkpoCheckDigit(okpo));
        } else {
            okpo.append(randomGenerator.nextInt(10));
        }

        return okpo.toString();
    }
}
