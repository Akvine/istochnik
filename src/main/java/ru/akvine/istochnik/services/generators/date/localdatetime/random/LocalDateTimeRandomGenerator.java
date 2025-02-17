package ru.akvine.istochnik.services.generators.date.localdatetime.random;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;
import ru.akvine.istochnik.services.generators.date.localdatetime.configs.LocalDateTimeGeneratorConfig;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class LocalDateTimeRandomGenerator extends AbstractRandomGenerator<LocalDateTime, LocalDateTimeGeneratorConfig> {

    @Override
    public List<LocalDateTime> generate(LocalDateTimeGeneratorConfig config) {
        List<LocalDateTime> generatedDates = new ArrayList<>();

        LocalDateTime startDate = config.getLocalDateTimeShiftRange().getStart();
        LocalDateTime endDate = config.getLocalDateTimeShiftRange().getEnd();


        while (generatedDates.size() != config.getSize()) {
            if (!config.isNotNull()) {
                boolean isNull = randomGenerator.nextBoolean();
                if (isNull) {
                    generatedDates.add(null);
                    continue;
                }
            }

            long startEpoch = startDate.toEpochSecond(java.time.ZoneOffset.UTC);
            long endEpoch = endDate.toEpochSecond(java.time.ZoneOffset.UTC);
            long randomEpoch = ThreadLocalRandom.current().nextLong(startEpoch, endEpoch);
            LocalDateTime generateDate = LocalDateTime.ofEpochSecond(randomEpoch, 0, java.time.ZoneOffset.UTC);

            if (generateDate.isAfter(endDate)) {
                continue;
            }

            if (config.isUnique()) {
                if (generatedDates.contains(generateDate)) {
                    continue;
                }
            }

            generatedDates.add(generateDate);
        }


        return generatedDates;
    }

    @Override
    public Class<?> getType() {
        return LocalDateTime.class;
    }
}
