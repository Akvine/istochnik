package ru.akvine.istochnik.services.generators.custom.requisite;

import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.utils.LuhnUtils;

@Service
public class OmsRandomGenerator extends AbstractRequisiteRandomGenerator<RequisiteGeneratorConfig> {
    // Полис ОМС единого образца: пятнадцать цифр номера и контрольная цифра
    private static final int PAYLOAD_LENGTH = 15;

    // Контрольная цифра единого номера полиса считается так же, как в алгоритме Луна.
    // Дата рождения и пол застрахованного в номере не кодируются
    @Override
    protected String generateValue(RequisiteGeneratorConfig config) {
        RandomGenerator randomGenerator = config.getRandomGenerator();

        StringBuilder oms = new StringBuilder();
        oms.append(randomGenerator.nextInt(1, 10));
        appendDigits(oms, PAYLOAD_LENGTH - 1, randomGenerator);

        if (config.isValid()) {
            oms.append(LuhnUtils.calculateCheckDigit(oms));
        } else {
            oms.append(randomGenerator.nextInt(10));
        }

        return oms.toString();
    }
}
