package ru.akvine.istochnik.services.generators.custom.imei;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ImeiRandomGenerator extends AbstractRandomGenerator<String, ImeiGeneratorConfig> {

    @Override
    public Collection<String> generate(ImeiGeneratorConfig config) {
        List<String> generatedValues = new ArrayList<>();

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

            StringBuilder imei = new StringBuilder();

            for (int i = 0; i < 14; i++) {
                imei.append(config.getRandomGenerator().nextInt(10));
            }

            if (config.isValid()) {
                imei.append(calculateCheckDigit(imei.toString()));
            } else {
                imei.append(config.getRandomGenerator().nextInt(10));
            }

            if (config.isUnique() && generatedValues.contains(imei.toString())) {
                iteration++;
                continue;
            }

            iteration++;
            generatedValues.add(imei.toString());
        }

        return generatedValues;
    }

    private static int calculateCheckDigit(String imei) {
        int sum = 0;
        for (int i = 0; i < imei.length(); i++) {
            int digit = Character.getNumericValue(imei.charAt(i));

            if (i % 2 == 0) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit / 10) + (digit % 10);
                }
            }

            sum += digit;
        }

        return (10 - (sum % 10)) % 10;
    }
}
