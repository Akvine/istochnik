package ru.akvine.istochnik.services.generators.custom.snils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;
import ru.akvine.istochnik.services.generators.custom.snils.configs.SnilsGeneratorConfig;

@Service
public class SnilsRandomGenerator extends AbstractRandomGenerator<Long, SnilsGeneratorConfig> {
    private static final long MIN_SNILS_LEFT_BOUND = 10000000000L;
    private static final long MIN_SNILS_RIGHT_BOUND = 99999999999L;
    // Номер без контрольного числа: девять цифр, первая не ноль, чтобы СНИЛС как число сохранял 11 разрядов
    private static final long MIN_NUMBER_LEFT_BOUND = 100000000L;
    private static final long MIN_NUMBER_RIGHT_BOUND = 1000000000L;

    @Override
    public Collection<Long> generate(SnilsGeneratorConfig config) {
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

            long generatedValue;
            if (config.isValid()) {
                // Контрольное число вычисляется, а не подбирается перебором: случайный номер валиден лишь в 1% случаев
                long number = config.getRandomGenerator().nextLong(MIN_NUMBER_LEFT_BOUND, MIN_NUMBER_RIGHT_BOUND);
                generatedValue = number * 100 + calculateChecksum(number);
            } else {
                generatedValue = config.getRandomGenerator().nextLong(MIN_SNILS_LEFT_BOUND, MIN_SNILS_RIGHT_BOUND);
            }

            if (config.isUnique() && !uniqueValues.add(generatedValue)) {
                iteration++;
                continue;
            }

            iteration++;
            generatedValues.add(generatedValue);
        }

        return generatedValues;
    }

    /**
     * Контрольное число СНИЛС для девятизначного номера
     */
    private int calculateChecksum(long number) {
        String numberPart = String.valueOf(number);

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (numberPart.charAt(i) - '0') * (9 - i);
        }

        int checksum;
        if (sum < 100) {
            checksum = sum;
        } else if (sum == 100 || sum == 101) {
            checksum = 0;
        } else {
            checksum = sum % 101;
            if (checksum == 100) {
                checksum = 0;
            }
        }

        return checksum;
    }
}
