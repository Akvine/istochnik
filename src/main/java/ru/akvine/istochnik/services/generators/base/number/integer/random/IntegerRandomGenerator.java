package ru.akvine.istochnik.services.generators.base.number.integer.random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;
import ru.akvine.istochnik.services.generators.base.number.integer.configs.IntegerGeneratorConfig;

@Service
public class IntegerRandomGenerator extends AbstractRandomGenerator<Long, IntegerGeneratorConfig> {

    @Override
    public Collection<Long> generate(IntegerGeneratorConfig config) {
        List<Long> generatedValues = new ArrayList<>();
        Set<Long> uniqueValues = new HashSet<>();

        Long start = config.getIntegerShiftRange().getStart();
        Long end = config.getIntegerShiftRange().getEnd();
        if (start == null) {
            start = 0L;
        }

        int iteration = 0;
        while (generatedValues.size() != config.getSize()) {
            checkGenerationCountAttempts(iteration, config.getSize());

            if (!config.isNotNull()) {
                boolean isNull = config.getRandomGenerator().nextBoolean();

                if (isNull) {
                    if (config.isUnique()) {
                        if (!uniqueValues.add(null)) {
                            iteration++;
                            continue;
                        } else {
                            generatedValues.add(null);
                        }
                    } else {
                        generatedValues.add(null);
                        iteration++;
                        continue;
                    }
                }
            }

            long generatedValue = config.getDistribution().nextLong(start, end, config.getRandomGenerator());

            if (config.isUnique() && !uniqueValues.add(generatedValue)) {
                iteration++;
                continue;
            }

            iteration++;
            generatedValues.add(generatedValue);
        }

        return generatedValues;
    }
}
