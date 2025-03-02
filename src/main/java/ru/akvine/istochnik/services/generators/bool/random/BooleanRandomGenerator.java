package ru.akvine.istochnik.services.generators.bool.random;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;
import ru.akvine.istochnik.services.generators.bool.configs.BooleanGeneratorConfig;

import java.util.ArrayList;
import java.util.List;

@Service
public class BooleanRandomGenerator extends AbstractRandomGenerator<Boolean, BooleanGeneratorConfig> {
    @Override
    public List<Boolean> generate(BooleanGeneratorConfig config) {
        List<Boolean> generatedValues = new ArrayList<>();

        while (generatedValues.size() != config.getSize()) {
            if (!config.isNotNull()) {
                boolean isNull = randomGenerator.nextBoolean();

                if (isNull) {
                    if (config.isUnique()) {
                        if (generatedValues.contains(null)) {
                            continue;
                        } else {
                            generatedValues.add(null);
                        }
                    } else {
                        generatedValues.add(null);
                        continue;
                    }
                }
            }

            boolean generatedValue = randomGenerator.nextBoolean();

            if (config.isUnique() && generatedValues.contains(generatedValue)) {
                continue;
            }

            generatedValues.add(generatedValue);
        }

        return generatedValues;
    }

    @Override
    public Class<?> getType() {
        return Boolean.class;
    }
}
