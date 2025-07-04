package ru.akvine.istochnik.services.generators.custom.time.random;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;
import ru.akvine.istochnik.services.generators.custom.time.configs.TimeGeneratorConfig;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TimeRandomGenerator extends AbstractRandomGenerator<LocalTime, TimeGeneratorConfig> {

    @Override
    public List<LocalTime> generate(TimeGeneratorConfig config) {
        List<LocalTime> generatedDates = new ArrayList<>();

        LocalTime startDate = config.getTimeShiftRange().getStart();
        LocalTime endDate = config.getTimeShiftRange().getEnd();

        int iteration = 0;
        while (generatedDates.size() != config.getSize()) {
            checkGenerationCountAttempts(iteration, config.getSize());

            if (!config.isNotNull()) {
                boolean isNull = config.getRandomGenerator().nextBoolean();
                if (isNull) {
                    generatedDates.add(null);
                    iteration++;
                    continue;
                }
            }

            int startSeconds = startDate.toSecondOfDay();
            int endSeconds = endDate.toSecondOfDay();

            int randomSeconds = ThreadLocalRandom.current().nextInt(startSeconds, endSeconds);
            LocalTime generateTime = LocalTime.ofSecondOfDay(randomSeconds);

            if (generateTime.isAfter(endDate)) {
                iteration++;
                continue;
            }

            if (config.isUnique()) {
                if (generatedDates.contains(generateTime)) {
                    iteration++;
                    continue;
                }
            }

            iteration++;
            generatedDates.add(generateTime);
        }


        return generatedDates;
    }
}
