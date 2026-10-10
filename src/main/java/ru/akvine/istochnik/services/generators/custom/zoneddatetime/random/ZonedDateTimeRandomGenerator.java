package ru.akvine.istochnik.services.generators.custom.zoneddatetime.random;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.exceptions.GenerationException;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;
import ru.akvine.istochnik.services.generators.custom.zoneddatetime.ZonedDateTimeShiftRange;
import ru.akvine.istochnik.services.generators.custom.zoneddatetime.configs.ZonedDateTimeGeneratorConfig;

@Service
public class ZonedDateTimeRandomGenerator extends AbstractRandomGenerator<ZonedDateTime, ZonedDateTimeGeneratorConfig> {

    @Override
    public List<ZonedDateTime> generate(ZonedDateTimeGeneratorConfig config) {
        int size = config.getSize();
        RandomGenerator randomGenerator = config.getRandomGenerator();
        ZonedDateTimeShiftRange range = config.getZonedDateTimeShiftRange();

        if (range.getEnd() == null) {
            throw new GenerationException("field [end] is required");
        }

        // Границы сравниваются как моменты времени, поэтому start и end могут быть заданы в разных зонах
        long startEpoch = range.getStart().toEpochSecond();
        long endEpoch = range.getEnd().toEpochSecond();
        if (startEpoch >= endEpoch) {
            throw new GenerationException("field [end] must be greater than field [start]");
        }
        ZoneId zone = range.getStart().getZone();

        List<ZonedDateTime> generatedValues = new ArrayList<>(size);
        Set<Long> uniqueEpochs = new HashSet<>();
        boolean nullGenerated = false;

        int iteration = 0;
        while (generatedValues.size() != size) {
            checkGenerationCountAttempts(iteration, size);
            iteration++;

            if (!config.isNotNull() && randomGenerator.nextBoolean()) {
                if (config.isUnique() && nullGenerated) {
                    continue;
                }

                nullGenerated = true;
                generatedValues.add(null);
                continue;
            }

            long randomEpoch = randomGenerator.nextLong(startEpoch, endEpoch);
            if (config.isUnique() && !uniqueEpochs.add(randomEpoch)) {
                continue;
            }

            generatedValues.add(ZonedDateTime.ofInstant(Instant.ofEpochSecond(randomEpoch), zone));
        }

        return generatedValues;
    }
}
