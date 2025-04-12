package ru.akvine.istochnik.services.generators.inn.org;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InnOrgGenerator extends AbstractRandomGenerator<Long, InnOrgGeneratorConfig> {
    @Override
    public Collection<Long> generate(InnOrgGeneratorConfig config) {
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
            int uniqueNumber = randomGenerator.nextInt(100000);

            String innWithoutControl = String.format("%04d%05d", taxOfficeCode, uniqueNumber);
            int controlDigit = calculateControlDigit(innWithoutControl);
            String generatedInn = innWithoutControl + controlDigit;

            if (config.isValid() && !isValidInn(generatedInn)) {
                iteration++;
                continue;
            }

            if (config.isUnique() && generatedValues.contains(Long.parseLong(generatedInn))) {
                iteration++;
                continue;
            }

            generatedValues.add(Long.parseLong(generatedInn));
        }

        return generatedValues;
    }

    @Override
    public Class<?> getType() {
        return Integer.class;
    }

    public static boolean isValidInn(String inn) {
        int expectedControlDigit = calculateControlDigit(inn.substring(0, 9));
        int actualControlDigit = Character.getNumericValue(inn.charAt(9));

        return expectedControlDigit == actualControlDigit;
    }

    private static int calculateControlDigit(String innWithoutControl) {
        int[] weights = {2, 4, 10, 3, 5, 9, 4, 6, 8};
        int sum = 0;

        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(innWithoutControl.charAt(i)) * weights[i];
        }

        return (sum % 11) % 10;
    }

}
