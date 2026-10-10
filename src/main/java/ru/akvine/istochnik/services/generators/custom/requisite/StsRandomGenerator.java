package ru.akvine.istochnik.services.generators.custom.requisite;

import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;

@Service
public class StsRandomGenerator extends AbstractVehicleDocumentRandomGenerator {

    // Серия свидетельства о регистрации бывает и цифровой, и буквенной
    @Override
    protected void appendSeries(StringBuilder builder, RandomGenerator randomGenerator) {
        if (randomGenerator.nextBoolean()) {
            appendSeriesDigits(builder, randomGenerator);
        } else {
            appendSeriesLetters(builder, randomGenerator);
        }
    }
}
