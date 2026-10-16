package ru.akvine.istochnik.services.generators.base.bool.random;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;
import ru.akvine.istochnik.services.generators.base.bool.configs.BooleanGeneratorConfig;

@Service
public class BooleanRandomGenerator extends AbstractRandomGenerator<Boolean, BooleanGeneratorConfig> {

    @Override
    public List<Boolean> generate(BooleanGeneratorConfig config) {
        List<Boolean> generatedValues = new ArrayList<>();
        Set<Boolean> uniqueValues = new HashSet<>();

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

            boolean generatedValue = config.getRandomGenerator().nextBoolean();
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
