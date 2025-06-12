package ru.akvine.istochnik.controllers.validators;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConverterDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.controllers.dto.validation.ValidationColumnsInfo;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.exceptions.validation.ConfigValidationException;
import ru.akvine.istochnik.providers.BaseTypeValidatorsProvider;
import ru.akvine.istochnik.validators.type.dto.ValidateAction;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GeneratorValidator {
    private final BaseTypeValidatorsProvider baseTypeValidatorsProvider;

    @Value("${max.dictionaries.per.column}")
    private int maxDictionariesPerColumn;
    @Value("${max.dictionary.elements.count}")
    private int maxDictionaryElementsCount;

    public void verifyGenerateTableRequest(GenerateTableRequest request) {
        Asserts.isNotNull(request);

        int rowsCount = request.getSize();
        List<ColumnDto> columns = request.getColumns();

        StringBuilder sb = new StringBuilder();
        if (rowsCount < 0) {
            sb.append("size can't be less than 0");
        }

        try {
            FileType.from(request.getFileType());
        } catch (RuntimeException exception) {
            if (StringUtils.isNotBlank(sb.toString())) {
                sb.append(". ");
            }
            sb.append(exception.getMessage());
        }

        ValidationColumnsInfo validationColumnsInfo = new ValidationColumnsInfo();
        for (ColumnDto column : columns) {
            String columnName = column.getName();

            GenerationStrategy strategy = null;
            try {
                strategy = GenerationStrategy.from(column.getGenerationStrategy());
            } catch (RuntimeException exception) {
                validationColumnsInfo.put(columnName, exception.getMessage());
            }

            if (strategy == GenerationStrategy.DICTIONARY) {
                if (column.getConfig().getDictionaries().size() > maxDictionariesPerColumn) {
                    validationColumnsInfo.put(
                            columnName,
                            "dictionaries count can't be more than max = [" + maxDictionariesPerColumn + "]");
                } else {
                    column.getConfig().getDictionaries().forEach(dictionary -> {
                        if (dictionary.size() > maxDictionaryElementsCount) {
                            String errorMessage = String.format(
                                    "Dictionary has more max limit = [%s] elements count = [%s]",
                                    dictionary.size(), maxDictionaryElementsCount
                            );
                            validationColumnsInfo.put(columnName, errorMessage);
                        }
                    });
                }
            }

            if (strategy == GenerationStrategy.ALGORITHM) {
                BaseType baseType = BaseType.from(column.getType());
                CustomType customType = CustomType.from(column.getType());

                if (Objects.isNull(baseType) && Objects.isNull(customType)) {
                    validationColumnsInfo.put(columnName, "field [type] has invalid value");
                    continue;
                }

                if (baseType == null) {
                    baseType = customType.getBaseType();
                }

                List<String> errors = baseTypeValidatorsProvider
                        .get(baseType)
                        .validate(columnName, buildValidateAction(rowsCount, column));

                if (CollectionUtils.isNotEmpty(errors)) {
                    validationColumnsInfo.put(columnName, errors);
                }
            }
        }

        if (StringUtils.isNotBlank(sb.toString()) || !validationColumnsInfo.getColumnNamesPerErrorMessages().isEmpty()) {
            throw new ConfigValidationException(sb.toString(), validationColumnsInfo);
        }
    }

    private ValidateAction buildValidateAction(int rowsCount, ColumnDto columnDto) {
        ValidateAction action = new ValidateAction()
                .setRowsCount(rowsCount)
                .setStart(columnDto.getConfig().getStart())
                .setEnd(columnDto.getConfig().getEnd())
                .setStep(columnDto.getConfig().getStep())
                .setRangeType(columnDto.getConfig().getRangeType());

        if (CollectionUtils.isNotEmpty(columnDto.getConverters())) {
            action.setConverters(columnDto.getConverters().stream()
                    .map(ConverterDto::getName).toList());
        }
        return action;
    }
}
