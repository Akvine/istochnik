package ru.akvine.istochnik.services.impl.generators.base;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.random.RandomGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.exceptions.GenerationException;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;

@Service
public class StringRandomGeneratorService extends AbstractBaseTypeGeneratorService {
    private static final char[] ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    @Value("${max.generation.attempts}")
    private int maxGenerationAttempts;

    @Autowired
    protected StringRandomGeneratorService(
            ConfigMapperServicesProvider configMappersProvider,
            ConverterConvertersProvider converterConvertersProvider) {
        super(configMappersProvider, converterConvertersProvider);
    }

    @Override
    public List<?> generate(Config config, List<Converter> converters) {
        int size = config.getSize();
        int length = resolveLength(config);
        RandomGenerator randomGenerator = config.getRandomGenerator();

        List<String> generatedValues = new ArrayList<>(size);
        Set<String> uniqueValues = new HashSet<>();
        boolean nullGenerated = false;

        long iteration = 0;
        long maxIterations = (long) size * maxGenerationAttempts;
        while (generatedValues.size() != size) {
            if (iteration++ > maxIterations) {
                String message = String.format(
                        "Max attempts to generation [%s] is exceeded for [%s] column type",
                        maxGenerationAttempts, getType().getValue());
                throw new GenerationException(message);
            }

            if (!config.isNotNull() && randomGenerator.nextBoolean()) {
                if (config.isUnique() && nullGenerated) {
                    continue;
                }

                nullGenerated = true;
                generatedValues.add(null);
                continue;
            }

            String generatedValue = generateString(length, randomGenerator);
            if (config.isUnique() && !uniqueValues.add(generatedValue)) {
                continue;
            }

            generatedValues.add(generatedValue);
        }

        return apply(generatedValues, converters, randomGenerator);
    }

    @Override
    public BaseType getType() {
        return BaseType.STRING;
    }

    // Длина строки задаётся полем length, для обратной совместимости допускается поле end
    private int resolveLength(Config config) {
        Integer length = config.getLength();
        if (length == null && StringUtils.isNotBlank(config.getEnd())) {
            try {
                length = Integer.parseInt(config.getEnd().trim());
            } catch (NumberFormatException exception) {
                throw new GenerationException("field [end] is not an integer");
            }
        }

        if (length == null || length <= 0) {
            throw new GenerationException("string length must be set by field [length] and be greater than 0");
        }

        return length;
    }

    private String generateString(int length, RandomGenerator randomGenerator) {
        char[] chars = new char[length];
        for (int i = 0; i < length; ++i) {
            chars[i] = ALPHABET[randomGenerator.nextInt(ALPHABET.length)];
        }

        return new String(chars);
    }
}
