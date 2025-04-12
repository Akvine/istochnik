package ru.akvine.istochnik.services.generators.datetime.random;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;
import ru.akvine.istochnik.services.generators.datetime.configs.DateTimeGeneratorConfig;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DateTimeRandomGenerator extends AbstractRandomGenerator<LocalDateTime, DateTimeGeneratorConfig> {

    @Override
    public List<LocalDateTime> generate(DateTimeGeneratorConfig config) {
        List<LocalDateTime> generatedDates = new ArrayList<>();

        LocalDateTime startDate = config.getDateTimeShiftRange().getStart();
        LocalDateTime endDate = config.getDateTimeShiftRange().getEnd();

        int iteration = 0;
        while (generatedDates.size() != config.getSize()) {
            checkGenerationCountAttempts(iteration, config.getSize());

            if (!config.isNotNull()) {
                boolean isNull = randomGenerator.nextBoolean();
                if (isNull) {
                    generatedDates.add(null);
                    iteration++;
                    continue;
                }
            }

            long startEpoch = startDate.toEpochSecond(java.time.ZoneOffset.UTC);
            long endEpoch = endDate.toEpochSecond(java.time.ZoneOffset.UTC);
            long randomEpoch = ThreadLocalRandom.current().nextLong(startEpoch, endEpoch);
            LocalDateTime generateDate = LocalDateTime.ofEpochSecond(randomEpoch, 0, java.time.ZoneOffset.UTC);

            if (generateDate.isAfter(endDate)) {
                iteration++;
                continue;
            }

            if (config.isUnique()) {
                if (generatedDates.contains(generateDate)) {
                    iteration++;
                    continue;
                }
            }

            iteration++;
            generatedDates.add(generateDate);
        }


        return generatedDates;
    }

    @Override
    public Class<?> getType() {
        return LocalDateTime.class;
    }
}
