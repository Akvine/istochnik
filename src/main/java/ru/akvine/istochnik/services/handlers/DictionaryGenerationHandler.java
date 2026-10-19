package ru.akvine.istochnik.services.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;
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
public class DictionaryGenerationHandler implements GenerationHandler {
    private static final double DEFAULT_WEIGHT = 1;

    private final ConverterConvertersProvider converterConvertersProvider;

    @Override
    public List<?> handle(GenerateColumn generateColumn) {
        Config config = generateColumn.getConfig();
        List<Converter> converters = generateColumn.getConverters();
        // Одинаковые элементы из разных словарей считаются одним значением
        List<String> dictionary = config.getDictionaries().stream()
                .flatMap(Collection::stream)
                .distinct()
                .toList();

        if (dictionary.isEmpty()) {
            throw new GenerationException("dictionaries can't be empty");
        }

        List<String> generatedValues;
        if (config.isUnique()) {
            generatedValues = generateUnique(config, dictionary);
        } else {
            generatedValues = generateNonUnique(config, dictionary);
        }

        return converterConvertersProvider
                .getByType(BaseType.STRING)
                .apply(generatedValues, converters, config.getRandomGenerator());
    }

    private List<String> generateUnique(Config config, List<String> dictionary) {
        int size = config.getSize();
        // Если колонка может содержать null, то он выступает ещё одним уникальным значением
        List<String> candidates = new ArrayList<>(dictionary);
        if (!config.isNotNull()) {
            candidates.add(null);
        }

        if (size > candidates.size()) {
            String errorMessage = String.format(
                    "Rows count = [%s] greater than available unique elements = [%s]", size, candidates.size());
            throw new GenerationException(errorMessage);
        }

        if (config.getRangeType() == RangeType.RANDOM) {
            Collections.shuffle(candidates, config.getRandomGenerator());
        }

        return new ArrayList<>(candidates.subList(0, size));
    }

    private List<String> generateNonUnique(Config config, List<String> dictionary) {
        int size = config.getSize();
        RandomGenerator randomGenerator = config.getRandomGenerator();
        boolean random = config.getRangeType() == RangeType.RANDOM;
        // Без весов значения выбираются так же, как до их появления: при том же seed получаются те же данные
        double[] cumulativeWeights = random && MapUtils.isNotEmpty(config.getWeights())
                ? cumulativeWeights(dictionary, config.getWeights())
                : null;

        List<String> generatedValues = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            if (!config.isNotNull() && randomGenerator.nextBoolean()) {
                generatedValues.add(null);
                continue;
            }

            int index;
            if (!random) {
                index = i % dictionary.size();
            } else if (cumulativeWeights == null) {
                index = randomGenerator.nextInt(dictionary.size());
            } else {
                index = chooseWeighted(cumulativeWeights, randomGenerator);
            }
            generatedValues.add(dictionary.get(index));
        }

        return generatedValues;
    }

    /**
     * Накопленные веса: i-й элемент - сумма весов значений с первого по i-е. Значения без веса получают вес 1
     */
    private double[] cumulativeWeights(List<String> dictionary, Map<String, Double> weights) {
        double[] cumulativeWeights = new double[dictionary.size()];
        double total = 0;
        for (int i = 0; i < dictionary.size(); ++i) {
            total += weights.getOrDefault(dictionary.get(i), DEFAULT_WEIGHT);
            cumulativeWeights[i] = total;
        }

        return cumulativeWeights;
    }

    /**
     * Случайная точка на отрезке от 0 до суммы весов попадает в промежуток одного из значений, и чем больше вес,
     * тем длиннее промежуток. Ищется первое значение, накопленный вес которого больше точки: значения с нулевым весом
     * так никогда не выбираются
     */
    private int chooseWeighted(double[] cumulativeWeights, RandomGenerator randomGenerator) {
        double point = randomGenerator.nextDouble(cumulativeWeights[cumulativeWeights.length - 1]);

        int low = 0;
        int high = cumulativeWeights.length - 1;
        while (low < high) {
            int middle = (low + high) >>> 1;
            if (cumulativeWeights[middle] > point) {
                high = middle;
            } else {
                low = middle + 1;
            }
        }

        return low;
    }

    @Override
    public GenerationStrategy getStrategy() {
        return GenerationStrategy.DICTIONARY;
    }
}
