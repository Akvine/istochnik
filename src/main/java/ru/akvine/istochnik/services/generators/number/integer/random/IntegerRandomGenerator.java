package ru.akvine.istochnik.services.generators.number.integer.random;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;
import ru.akvine.istochnik.services.generators.number.integer.configs.IntegerGeneratorConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class IntegerRandomGenerator extends AbstractRandomGenerator<Integer, IntegerGeneratorConfig> {
    @Override
    public Collection<Integer> generate(IntegerGeneratorConfig config) {
        List<Integer> generatedValues = new ArrayList<>();

        Integer start = config.getIntegerRange().getStart();
        int end = config.getIntegerRange().getEnd();
        if (start == null) {
            start = 0;
        }

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

            int generatedValue = randomGenerator.nextInt(start, end);

            if (config.isUnique() && generatedValues.contains(generatedValue)) {
                continue;
            }

            generatedValues.add(generatedValue);
        }

        return generatedValues;
    }

    @Override
    public Class<?> getType() {
        return Integer.class;
    }
}
