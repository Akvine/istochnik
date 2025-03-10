package ru.akvine.istochnik.services.generators.number.doubles.random;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;
import ru.akvine.istochnik.services.generators.number.doubles.configs.DoubleGeneratorConfig;

import java.util.ArrayList;
import java.util.List;

@Service
public class DoubleRandomGenerator extends AbstractRandomGenerator<Double, DoubleGeneratorConfig> {
    @Override
    public List<Double> generate(DoubleGeneratorConfig config) {
        List<Double> generatedValues = new ArrayList<>();

        Double start = config.getDoubleShiftRange().getStart();
        double end = config.getDoubleShiftRange().getEnd();
        if (start == null) {
            start = 0D;
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

            double generatedValue = randomGenerator.nextDouble(start, end);

            if (config.isUnique() && generatedValues.contains(generatedValue)) {
                continue;
            }

            generatedValues.add(generatedValue);
        }

        return generatedValues;
    }

    @Override
    public Class<?> getType() {
        return Double.class;
    }
}
