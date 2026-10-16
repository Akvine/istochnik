package ru.akvine.istochnik.services.generators.custom.ogrn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;

@Service
public class OgrnRandomGenerator extends AbstractRandomGenerator<Long, OgrnGeneratorConfig> {

    @Override
    public Collection<Long> generate(OgrnGeneratorConfig config) {
        List<Long> generatedValues = new ArrayList<>();
        Set<Long> uniqueValues = new HashSet<>();

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

            StringBuilder ogrnBuilder = new StringBuilder("1");

            while (ogrnBuilder.length() < 12) {
                ogrnBuilder.append(config.getRandomGenerator().nextInt(10));
            }

            long ogrn12 = Long.parseLong(ogrnBuilder.toString());
            long controlDigit = (ogrn12 % 11) % 10;

            if (config.isValid()) {
                ogrnBuilder.append(controlDigit);
            } else {
                ogrnBuilder.append(config.getRandomGenerator().nextInt(10));
            }

            if (config.isUnique() && !uniqueValues.add(Long.parseLong(ogrnBuilder.toString()))) {
                iteration++;
                continue;
            }

            iteration++;
            generatedValues.add(Long.parseLong(ogrnBuilder.toString()));
        }

        return generatedValues;
    }
}
