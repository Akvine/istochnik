package ru.akvine.istochnik.services.generators.base.bool.random;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;
import ru.akvine.istochnik.services.generators.base.bool.configs.BooleanGeneratorConfig;

import java.util.ArrayList;
import java.util.List;

@Service
public class BooleanRandomGenerator extends AbstractRandomGenerator<Boolean, BooleanGeneratorConfig> {
    @Override
    public List<Boolean> generate(BooleanGeneratorConfig config) {
        List<Boolean> generatedValues = new ArrayList<>();

        int iteration = 0;
        while (generatedValues.size() != config.getSize()) {
            checkGenerationCountAttempts(iteration, config.getSize());

            if (!config.isNotNull()) {
                boolean isNull = randomGenerator.nextBoolean();

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

            boolean generatedValue = randomGenerator.nextBoolean();
            if (config.isUnique() && generatedValues.contains(generatedValue)) {
                iteration++;
                continue;
            }

            iteration++;
            generatedValues.add(generatedValue);
        }

        return generatedValues;
    }

    @Override
    public Class<?> getType() {
        return Boolean.class;
    }
}
