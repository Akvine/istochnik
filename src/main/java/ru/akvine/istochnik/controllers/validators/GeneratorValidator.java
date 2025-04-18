package ru.akvine.istochnik.controllers.validators;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.FilterDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.controllers.dto.validation.ValidationColumnsInfo;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.exceptions.validation.ConfigValidationException;
import ru.akvine.istochnik.managers.BaseTypeValidatorsManager;
import ru.akvine.istochnik.validators.type.dto.ValidateAction;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GeneratorValidator {
    private final BaseTypeValidatorsManager baseTypeValidatorsManager;

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

                List<String> errors = baseTypeValidatorsManager
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

        if (CollectionUtils.isNotEmpty(columnDto.getFilters())) {
            action.setFilters(columnDto.getFilters().stream()
                    .map(FilterDto::getName).toList());
        }
        return action;
    }
}
