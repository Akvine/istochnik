package ru.akvine.istochnik.services.generators.custom.inn.personal;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class InnPersGenerator extends AbstractRandomGenerator<Long, InnPersGeneratorConfig> {
    @Override
    public Collection<Long> generate(InnPersGeneratorConfig config) {
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

            int taxOfficeCode = 100 + randomGenerator.nextInt(9900);
            int uniqueNumber = randomGenerator.nextInt(1_000_000);

            String innWithoutControl = String.format("%04d%06d", taxOfficeCode, uniqueNumber);

            int controlDigit1 = calculateControlDigit(innWithoutControl, true);
            int controlDigit2 = calculateControlDigit(innWithoutControl + controlDigit1, false);

            String generatedInn = innWithoutControl + controlDigit1 + controlDigit2;

            if (config.isValid() && !isValidInn(generatedInn)) {
                iteration++;
                continue;
            }

            if (config.isUnique() && generatedValues.contains(Long.parseLong(generatedInn))) {
                iteration++;
                continue;
            }

            iteration++;
            generatedValues.add(Long.parseLong(generatedInn));
        }

        return generatedValues;
    }

    @Override
    public Class<?> getType() {
        return Long.class;
    }

    public static boolean isValidInn(String inn) {
        int expectedControlDigit1 = calculateControlDigit(inn.substring(0, 10), true);
        int expectedControlDigit2 = calculateControlDigit(inn.substring(0, 10) + expectedControlDigit1, false);
        int actualControlDigit1 = Character.getNumericValue(inn.charAt(10));
        int actualControlDigit2 = Character.getNumericValue(inn.charAt(11));

        return expectedControlDigit1 == actualControlDigit1 && expectedControlDigit2 == actualControlDigit2;
    }

    private static int calculateControlDigit(String innPart, boolean first) {
        int[] weights = first
                ? new int[]{2, 4, 10, 3, 5, 9, 4, 6, 8, 0}
                : new int[]{3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8};

        int sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += Character.getNumericValue(innPart.charAt(i)) * weights[i];
        }

        return (sum % 11) % 10;
    }
}
