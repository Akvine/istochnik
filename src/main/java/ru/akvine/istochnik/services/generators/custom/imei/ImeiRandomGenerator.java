package ru.akvine.istochnik.services.generators.custom.imei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;
import ru.akvine.istochnik.utils.LuhnUtils;

@Service
public class ImeiRandomGenerator extends AbstractRandomGenerator<String, ImeiGeneratorConfig> {

    @Override
    public Collection<String> generate(ImeiGeneratorConfig config) {
        List<String> generatedValues = new ArrayList<>();
        Set<String> uniqueValues = new HashSet<>();

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

            StringBuilder imei = new StringBuilder();

            for (int i = 0; i < 14; i++) {
                imei.append(config.getRandomGenerator().nextInt(10));
            }

            if (config.isValid()) {
                imei.append(LuhnUtils.calculateCheckDigit(imei));
            } else {
                imei.append(config.getRandomGenerator().nextInt(10));
            }

            if (config.isUnique() && !uniqueValues.add(imei.toString())) {
                iteration++;
                continue;
            }

            iteration++;
            generatedValues.add(imei.toString());
        }

        return generatedValues;
    }
}
