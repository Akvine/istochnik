package ru.akvine.istochnik.services.generators.ogrn;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class OgrnRandomGenerator extends AbstractRandomGenerator<Long, OgrnGeneratorConfig> {
    @Override
    public Collection<Long> generate(OgrnGeneratorConfig config) {
        List<Long> generatedValues = new ArrayList<>();

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

            StringBuilder ogrnBuilder = new StringBuilder("1");

            while (ogrnBuilder.length() < 12) {
                ogrnBuilder.append(randomGenerator.nextInt(10));
            }

            long ogrn12 = Long.parseLong(ogrnBuilder.toString());
            long controlDigit = (ogrn12 % 11) % 10;

            ogrnBuilder.append(controlDigit);

            if (config.isValid()) {
                iteration++;
                continue;
            }

            if (config.isUnique() && generatedValues.contains(Long.parseLong(ogrnBuilder.toString()))) {
                iteration++;
                continue;
            }

            iteration++;
            generatedValues.add(Long.parseLong(ogrnBuilder.toString()));
        }

        return generatedValues;
    }

    @Override
    public Class<?> getType() {
        return Integer.class;
    }
}
