package ru.akvine.istochnik.services.generators.custom.requisite;

import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;

@Service
public class PtsRandomGenerator extends AbstractVehicleDocumentRandomGenerator {

    // Серия бумажного паспорта транспортного средства всегда буквенная
    @Override
    protected void appendSeries(StringBuilder builder, RandomGenerator randomGenerator) {
        appendSeriesLetters(builder, randomGenerator);
    }
}
