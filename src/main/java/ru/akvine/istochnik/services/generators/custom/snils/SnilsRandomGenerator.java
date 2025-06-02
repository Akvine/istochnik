package ru.akvine.istochnik.services.generators.custom.snils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;
import ru.akvine.istochnik.services.generators.custom.snils.configs.SnilsGeneratorConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SnilsRandomGenerator extends AbstractRandomGenerator<Long, SnilsGeneratorConfig> {
    private static final long MIN_SNILS_LEFT_BOUND = 10000000000L;
    private static final long MIN_SNILS_RIGHT_BOUND = 99999999999L;

    @Override
    public Collection<Long> generate(SnilsGeneratorConfig config) {
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

            long generatedValue = randomGenerator.nextLong(MIN_SNILS_LEFT_BOUND, MIN_SNILS_RIGHT_BOUND);

            if (config.isValid() && !isValidSnils(generatedValue)) {
                iteration++;
                continue;
            }

            if (config.isUnique() && generatedValues.contains(generatedValue)) {
                iteration++;
                continue;
            }

            iteration++;
            generatedValues.add(generatedValue);
        }

        return generatedValues;
    }

    private boolean isValidSnils(long snils) {
        String snilsStr = String.format("%011d", snils);

        if (snilsStr.length() != 11) {
            return false;
        }

        String numberPart = snilsStr.substring(0, 9);
        int expectedChecksum = Integer.parseInt(snilsStr.substring(9, 11));

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (numberPart.charAt(i) - '0') * (9 - i);
        }

        int actualChecksum;
        if (sum < 100) {
            actualChecksum = sum;
        } else if (sum == 100 || sum == 101) {
            actualChecksum = 0;
        } else {
            actualChecksum = sum % 101;
            if (actualChecksum == 100) {
                actualChecksum = 0;
            }
        }

        return expectedChecksum == actualChecksum;
    }
}
