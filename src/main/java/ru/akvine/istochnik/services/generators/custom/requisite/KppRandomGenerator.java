package ru.akvine.istochnik.services.generators.custom.requisite;

import java.util.List;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;

@Service
public class KppRandomGenerator extends AbstractRequisiteRandomGenerator<RequisiteGeneratorConfig> {
    // Распространённые коды причины постановки на учёт
    private static final List<String> REASON_CODES =
            List.of("01", "02", "03", "04", "05", "31", "32", "43", "44", "45", "50");

    // Формат КПП: код налогового органа (регион и инспекция), причина постановки на учёт, порядковый номер.
    // Контрольного разряда у КПП нет
    @Override
    protected String generateValue(RequisiteGeneratorConfig config) {
        RandomGenerator randomGenerator = config.getRandomGenerator();

        StringBuilder kpp = new StringBuilder();
        appendNumber(kpp, 1, 99, 2, randomGenerator);
        appendNumber(kpp, 1, 99, 2, randomGenerator);
        kpp.append(REASON_CODES.get(randomGenerator.nextInt(REASON_CODES.size())));
        appendNumber(kpp, 1, 999, 3, randomGenerator);

        return kpp.toString();
    }
}
