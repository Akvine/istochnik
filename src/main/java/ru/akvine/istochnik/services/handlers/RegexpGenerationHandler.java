package ru.akvine.istochnik.services.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.random.RandomGenerator;
import lombok.RequiredArgsConstructor;
import nl.flotsam.xeger.Xeger;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.exceptions.GenerationException;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.GenerationHandler;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.dto.GenerateColumn;

@Service
@RequiredArgsConstructor
public class RegexpGenerationHandler implements GenerationHandler {
    private final ConverterConvertersProvider converterConvertersProvider;
    private final UniqueValuesGenerator uniqueValuesGenerator;

    @Override
    public List<?> handle(GenerateColumn generateColumn) {
        Config config = generateColumn.getConfig();
        List<Converter> converters = generateColumn.getConverters();
        int size = config.getSize();
        RandomGenerator randomGenerator = config.getRandomGenerator();
        boolean random = config.getRangeType() == RangeType.RANDOM;

        if (CollectionUtils.isEmpty(config.getRegexps())) {
            throw new GenerationException("regexps can't be empty");
        }

        // Автомат для регулярного выражения строится один раз, а не на каждую строку
        List<Xeger> generators = config.getRegexps().stream()
                .map(regexp -> new Xeger(regexp, (Random) randomGenerator))
                .toList();

        // По порядку выражения чередуются по номеру строки, а случайно выбираются для каждого значения
        IntFunction<String> valueForRow = row -> {
            int index = random ? randomGenerator.nextInt(generators.size()) : row % generators.size();
            return generators.get(index).generate();
        };

        List<String> generatedValues;
        if (config.isUnique()) {
            generatedValues = uniqueValuesGenerator.generate(config, getStrategy(), valueForRow);
        } else {
            generatedValues = new ArrayList<>(size);
            for (int i = 0; i < size; ++i) {
                if (!config.isNotNull() && randomGenerator.nextBoolean()) {
                    generatedValues.add(null);
                    continue;
                }

                generatedValues.add(valueForRow.apply(i));
            }
        }

        return converterConvertersProvider
                .getByType(BaseType.STRING)
                .apply(generatedValues, converters, randomGenerator);
    }

    @Override
    public GenerationStrategy getStrategy() {
        return GenerationStrategy.REGEXP;
    }
}
