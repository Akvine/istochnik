package ru.akvine.istochnik.services.generators.custom.date.random;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;
import ru.akvine.istochnik.services.generators.custom.date.configs.DateGeneratorConfig;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DateRandomGenerator extends AbstractRandomGenerator<LocalDate, DateGeneratorConfig> {
    @Override
    public List<LocalDate> generate(DateGeneratorConfig config) {
        List<LocalDate> generatedDates = new ArrayList<>();

        LocalDate startDate = config.getDateShiftRange().getStart();
        LocalDate endDate = config.getDateShiftRange().getEnd();

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

            long startEpochDay = startDate.toEpochDay();
            long endEpochDay = endDate.toEpochDay();
            LocalDate generateDate = LocalDate.ofEpochDay(
                    ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay)
            );

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
        return LocalDate.class;
    }
}
