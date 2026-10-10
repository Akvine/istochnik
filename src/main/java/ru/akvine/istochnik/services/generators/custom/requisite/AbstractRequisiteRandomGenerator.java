package ru.akvine.istochnik.services.generators.custom.requisite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.random.RandomGenerator;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;

/**
 * Общая часть генераторов строковых реквизитов: обработка notNull и unique.
 * Наследник описывает только то, как строится одно значение
 */
public abstract class AbstractRequisiteRandomGenerator<C extends RequisiteGeneratorConfig>
        extends AbstractRandomGenerator<String, C> {

    @Override
    public List<String> generate(C config) {
        int size = config.getSize();
        RandomGenerator randomGenerator = config.getRandomGenerator();

        List<String> generatedValues = new ArrayList<>(size);
        Set<String> uniqueValues = new HashSet<>();
        boolean nullGenerated = false;

        int iteration = 0;
        while (generatedValues.size() != size) {
            checkGenerationCountAttempts(iteration, size);
            iteration++;

            if (!config.isNotNull() && randomGenerator.nextBoolean()) {
                if (config.isUnique() && nullGenerated) {
                    continue;
                }

                nullGenerated = true;
                generatedValues.add(null);
                continue;
            }

            String generatedValue = generateValue(config);
            if (config.isUnique() && !uniqueValues.add(generatedValue)) {
                continue;
            }

            generatedValues.add(generatedValue);
        }

        return generatedValues;
    }

    protected abstract String generateValue(C config);

    protected void appendDigits(StringBuilder builder, int count, RandomGenerator randomGenerator) {
        for (int i = 0; i < count; i++) {
            builder.append(randomGenerator.nextInt(10));
        }
    }

    /**
     * Добавляет случайное число из [from, to], дополненное слева нулями до width цифр
     */
    protected void appendNumber(StringBuilder builder, int from, int to, int width, RandomGenerator randomGenerator) {
        String number = String.valueOf(randomGenerator.nextInt(from, to + 1));
        builder.append("0".repeat(width - number.length())).append(number);
    }
}
