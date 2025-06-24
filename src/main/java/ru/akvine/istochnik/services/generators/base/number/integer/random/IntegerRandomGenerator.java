package ru.akvine.istochnik.services.generators.base.number.integer.random;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;
import ru.akvine.istochnik.services.generators.base.number.integer.configs.IntegerGeneratorConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class IntegerRandomGenerator extends AbstractRandomGenerator<Long, IntegerGeneratorConfig> {

    @Override
    public Collection<Long> generate(IntegerGeneratorConfig config) {
        List<Long> generatedValues = new ArrayList<>();

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
                        if (generatedValues.contains(null)) {
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

            long generatedValue = config.getRandomGenerator().nextLong(start, end);

            if (config.isUnique() && generatedValues.contains(generatedValue)) {
                iteration++;
                continue;
            }

            iteration++;
            generatedValues.add(generatedValue);
        }

        return generatedValues;
    }
}
