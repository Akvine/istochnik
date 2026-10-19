package ru.akvine.istochnik.validators.strategy;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.validation.ValidationColumnsInfo;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.providers.BaseTypeValidatorsProvider;

@Component
public class DictionaryStrategyValidator extends AbstractGenerationStrategyValidator {
    private static final double DEFAULT_WEIGHT = 1;
    private static final int SHOWN_VALUES_COUNT = 10;

    @Value("${max.dictionaries.per.column}")
    private int maxDictionariesPerColumn;

    @Value("${max.dictionary.elements.count}")
    private int maxDictionaryElementsCount;

    @Autowired
    public DictionaryStrategyValidator(BaseTypeValidatorsProvider baseTypeValidatorsProvider) {
        super(baseTypeValidatorsProvider);
    }

    @Override
    public void validate(ValidationColumnsInfo validationColumnsInfo, ColumnDto column, int rowsCount) {
        String columnName = column.getName();

        Set<Set<String>> dictionaries = column.getConfig().getDictionaries();
        if (CollectionUtils.isEmpty(dictionaries) || dictionaries.stream().allMatch(CollectionUtils::isEmpty)) {
            validationColumnsInfo.put(columnName, "field [dictionaries] can't be empty");
            return;
        }

        if (column.getConfig().getDictionaries().size() > maxDictionariesPerColumn) {
            validationColumnsInfo.put(
                    columnName, "dictionaries count can't be more than max = [" + maxDictionariesPerColumn + "]");
        } else {
            column.getConfig().getDictionaries().forEach(dictionary -> {
                if (dictionary.size() > maxDictionaryElementsCount) {
                    String errorMessage = String.format(
                            "Dictionary has more max limit = [%s] elements count = [%s]",
                            dictionary.size(), maxDictionaryElementsCount);
                    validationColumnsInfo.put(columnName, errorMessage);
                }
            });
        }

        Map<String, Double> weights = column.getConfig().getWeights();
        if (MapUtils.isNotEmpty(weights)) {
            validateWeights(validationColumnsInfo, column, dictionaries, weights);
        }

        if (column.getConfig().isUnique()) {
            // Одинаковые элементы из разных словарей считаются одним значением
            int allElementsCount = (int)
                    dictionaries.stream().flatMap(Set::stream).distinct().count();

            // Если колонока может содержать null, но unique = true, то добавляем доп. значение
            if (!column.getConfig().isNotNull()) {
                allElementsCount += 1;
            }

            if (rowsCount > allElementsCount) {
                String errorMessage = String.format(
                        "Rows count = [%s] greater than available unique elements = [%s]. "
                                + "Disable field \"unique\": \"true\", increase dictionaries elements or reduce rows count",
                        rowsCount, allElementsCount);
                validationColumnsInfo.put(columnName, errorMessage);
            }
        }

        List<String> errors = baseTypeValidatorsProvider
                .get(BaseType.STRING)
                .validate(columnName, buildValidateAction(rowsCount, column));
        if (CollectionUtils.isNotEmpty(errors)) {
            validationColumnsInfo.put(columnName, errors);
        }
    }

    private void validateWeights(
            ValidationColumnsInfo validationColumnsInfo,
            ColumnDto column,
            Set<Set<String>> dictionaries,
            Map<String, Double> weights) {
        String columnName = column.getName();

        // По порядку значения перебираются без случайности, а уникальные значения выбираются по одному разу
        if (!RangeType.RANDOM.getType().equalsIgnoreCase(column.getConfig().getRangeType())) {
            validationColumnsInfo.put(columnName, "field [weights] can be used only with rangeType = [random]");
        }
        if (column.getConfig().isUnique()) {
            validationColumnsInfo.put(columnName, "field [weights] can't be used together with unique = true");
        }

        Set<String> values = dictionaries.stream().flatMap(Set::stream).collect(Collectors.toSet());
        List<String> unknownValues = weights.keySet().stream()
                .filter(value -> !values.contains(value))
                .sorted()
                .limit(SHOWN_VALUES_COUNT)
                .toList();
        if (!unknownValues.isEmpty()) {
            validationColumnsInfo.put(
                    columnName, "field [weights] contains values which are absent in dictionaries: " + unknownValues);
        }

        List<String> invalidValues = weights.entrySet().stream()
                .filter(entry -> entry.getValue() == null
                        || !Double.isFinite(entry.getValue())
                        || entry.getValue() < 0)
                .map(Map.Entry::getKey)
                .sorted()
                .limit(SHOWN_VALUES_COUNT)
                .toList();
        if (!invalidValues.isEmpty()) {
            validationColumnsInfo.put(
                    columnName, "weights must be finite numbers not less than 0, invalid weights of: " + invalidValues);
            return;
        }

        // Значения без веса получают вес 1
        double totalWeight = values.stream()
                .mapToDouble(value -> weights.getOrDefault(value, DEFAULT_WEIGHT))
                .sum();
        if (totalWeight <= 0) {
            validationColumnsInfo.put(columnName, "at least one dictionary value must have weight greater than 0");
        } else if (!Double.isFinite(totalWeight)) {
            validationColumnsInfo.put(columnName, "sum of weights is too large");
        }
    }

    @Override
    public GenerationStrategy getStrategy() {
        return GenerationStrategy.DICTIONARY;
    }
}
