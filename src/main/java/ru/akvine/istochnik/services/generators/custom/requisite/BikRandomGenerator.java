package ru.akvine.istochnik.services.generators.custom.requisite;

import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;

@Service
public class BikRandomGenerator extends AbstractRequisiteRandomGenerator<RequisiteGeneratorConfig> {
    private static final String RUSSIA_CODE = "04";

    // Формат БИК: код России, код территории, номер подразделения Банка России,
    // условный номер кредитной организации (от 050 до 999). Контрольного разряда у БИК нет
    @Override
    protected String generateValue(RequisiteGeneratorConfig config) {
        RandomGenerator randomGenerator = config.getRandomGenerator();

        StringBuilder bik = new StringBuilder(RUSSIA_CODE);
        appendNumber(bik, 1, 99, 2, randomGenerator);
        appendNumber(bik, 0, 99, 2, randomGenerator);
        appendNumber(bik, 50, 999, 3, randomGenerator);

        return bik.toString();
    }
}
