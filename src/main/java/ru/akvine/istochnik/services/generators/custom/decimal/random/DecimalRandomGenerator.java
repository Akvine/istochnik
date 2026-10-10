package ru.akvine.istochnik.services.generators.custom.decimal.random;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.exceptions.GenerationException;
import ru.akvine.istochnik.services.generators.AbstractRandomGenerator;
import ru.akvine.istochnik.services.generators.custom.decimal.configs.DecimalGeneratorConfig;

@Service
public class DecimalRandomGenerator extends AbstractRandomGenerator<BigDecimal, DecimalGeneratorConfig> {

    @Override
    public List<BigDecimal> generate(DecimalGeneratorConfig config) {
        int size = config.getSize();
        int scale = config.getScale();
        RandomGenerator randomGenerator = config.getRandomGenerator();

        if (config.getDecimalShiftRange().getEnd() == null) {
            throw new GenerationException("field [end] is required");
        }

        // Значения выбираются равномерно из [start, end) с шагом в одну единицу младшего разряда
        BigInteger start =
                config.getDecimalShiftRange().getStart().setScale(scale).unscaledValue();
        BigInteger end = config.getDecimalShiftRange().getEnd().setScale(scale).unscaledValue();
        BigInteger valuesCount = end.subtract(start);
        if (valuesCount.signum() <= 0) {
            throw new GenerationException("field [end] must be greater than field [start]");
        }

        if (config.isUnique()) {
            // Если колонка может содержать null, то он выступает ещё одним уникальным значением
            BigInteger uniqueValuesCount = config.isNotNull() ? valuesCount : valuesCount.add(BigInteger.ONE);
            if (BigInteger.valueOf(size).compareTo(uniqueValuesCount) > 0) {
                String errorMessage = String.format(
                        "Rows count = [%s] greater than available unique values = [%s]", size, uniqueValuesCount);
                throw new GenerationException(errorMessage);
            }
        }

        List<BigDecimal> generatedValues = new ArrayList<>(size);
        Set<BigDecimal> uniqueValues = new HashSet<>();
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

            BigDecimal generatedValue = new BigDecimal(start.add(nextBigInteger(valuesCount, randomGenerator)), scale);
            if (config.isUnique() && !uniqueValues.add(generatedValue)) {
                continue;
            }

            generatedValues.add(generatedValue);
        }

        return generatedValues;
    }

    /**
     * Равномерно распределённое значение из [0, bound)
     */
    private BigInteger nextBigInteger(BigInteger bound, RandomGenerator randomGenerator) {
        int bitLength = bound.bitLength();
        byte[] bytes = new byte[(bitLength + 7) / 8];
        int excessBits = bytes.length * 8 - bitLength;

        BigInteger value;
        do {
            randomGenerator.nextBytes(bytes);
            bytes[0] &= (byte) (0xFF >>> excessBits);
            value = new BigInteger(1, bytes);
        } while (value.compareTo(bound) >= 0);

        return value;
    }
}
