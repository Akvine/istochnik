package ru.akvine.istochnik.validators.strategy;

import java.util.List;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.istochnik.controllers.dto.validation.ValidationColumnsInfo;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.providers.BaseTypeValidatorsProvider;

@Component
public class DictionaryStrategyValidator extends AbstractGenerationStrategyValidator {

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

        if (column.getConfig().isUnique()) {
            int allElementsCount = 0;
            for (Set<String> dictionary : column.getConfig().getDictionaries()) {
                allElementsCount += dictionary.size();
            }

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

    @Override
    public GenerationStrategy getStrategy() {
        return GenerationStrategy.DICTIONARY;
    }
}
