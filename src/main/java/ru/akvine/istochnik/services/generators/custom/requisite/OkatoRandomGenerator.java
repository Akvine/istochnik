package ru.akvine.istochnik.services.generators.custom.requisite;

import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;

@Service
public class OkatoRandomGenerator extends AbstractTerritoryCodeRandomGenerator {

    // Формат ОКАТО населённого пункта (11 цифр): субъект РФ, район или город, сельсовет или посёлок,
    // населённый пункт. Контрольное число в сам код не входит
    @Override
    protected String generateValue(RequisiteGeneratorConfig config) {
        RandomGenerator randomGenerator = config.getRandomGenerator();

        StringBuilder okato = new StringBuilder();
        appendRegion(okato, randomGenerator);

        if (randomGenerator.nextBoolean()) {
            // Район: внутри него посёлки городского типа (5) или сельсоветы (8)
            appendNumber(okato, 201, 299, 3, randomGenerator);
            okato.append(randomGenerator.nextBoolean() ? '5' : '8');
        } else {
            // Город: внутри него городские районы (3) или подчинённые посёлки (5)
            appendNumber(okato, 401, 499, 3, randomGenerator);
            okato.append(randomGenerator.nextBoolean() ? '3' : '5');
        }
        appendNumber(okato, 1, 99, 2, randomGenerator);
        appendNumber(okato, 1, 199, 3, randomGenerator);

        return okato.toString();
    }
}
