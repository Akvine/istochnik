package ru.akvine.istochnik.services.generators.custom.requisite;

import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.utils.RequisiteUtils;

@Service
public class OgrnipRandomGenerator extends AbstractRequisiteRandomGenerator<RequisiteGeneratorConfig> {
    // Признак индивидуального предпринимателя
    private static final String SIGN = "3";
    // ОГРНИП присваиваются с 2004 года. Верхняя граница фиксирована, чтобы результат по seed не зависел от даты
    private static final int MIN_YEAR = 4;
    private static final int MAX_YEAR = 25;

    // Формат ОГРНИП: признак, две цифры года, код региона, порядковый номер, контрольная цифра
    @Override
    protected String generateValue(RequisiteGeneratorConfig config) {
        RandomGenerator randomGenerator = config.getRandomGenerator();

        StringBuilder ogrnip = new StringBuilder(SIGN);
        appendNumber(ogrnip, MIN_YEAR, MAX_YEAR, 2, randomGenerator);
        appendNumber(ogrnip, 1, 99, 2, randomGenerator);
        appendDigits(ogrnip, 9, randomGenerator);

        if (config.isValid()) {
            ogrnip.append(RequisiteUtils.calculateOgrnipCheckDigit(ogrnip));
        } else {
            ogrnip.append(randomGenerator.nextInt(10));
        }

        return ogrnip.toString();
    }
}
