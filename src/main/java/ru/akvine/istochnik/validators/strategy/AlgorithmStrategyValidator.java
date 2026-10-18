package ru.akvine.istochnik.validators.strategy;

import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.validation.ValidationColumnsInfo;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.providers.BaseTypeValidatorsProvider;
import ru.akvine.istochnik.providers.CustomTypeValidatorsProvider;
import ru.akvine.istochnik.validators.DistributionValidator;
import ru.akvine.istochnik.validators.custom.CustomTypeValidator;

@Component
public class AlgorithmStrategyValidator extends AbstractGenerationStrategyValidator {
    private final CustomTypeValidatorsProvider customTypeValidatorsProvider;
    private final DistributionValidator distributionValidator;

    @Autowired
    public AlgorithmStrategyValidator(
            BaseTypeValidatorsProvider baseTypeValidatorsProvider,
            CustomTypeValidatorsProvider customTypeValidatorsProvider,
            DistributionValidator distributionValidator) {
        super(baseTypeValidatorsProvider);
        this.customTypeValidatorsProvider = customTypeValidatorsProvider;
        this.distributionValidator = distributionValidator;
    }

    @Override
    public void validate(ValidationColumnsInfo validationColumnsInfo, ColumnDto column, int rowsCount) {
        String columnName = column.getName();

        BaseType baseType = BaseType.from(column.getType());
        CustomType customType = CustomType.from(column.getType());

        if (Objects.isNull(baseType) && Objects.isNull(customType)) {
            validationColumnsInfo.put(columnName, "field [type] has invalid value");
            return;
        }

        if (baseType == BaseType.STRING && !hasValidStringLength(column)) {
            validationColumnsInfo.put(columnName, "field [length] must be set and be greater than 0");
        }

        if (baseType == null) {
            baseType = customType.getBaseType();
        }

        // TODO: добавить валидацию для boolean касательно unique = true и notNull

        List<String> errors =
                baseTypeValidatorsProvider.get(baseType).validate(columnName, buildValidateAction(rowsCount, column));

        if (CollectionUtils.isNotEmpty(errors)) {
            validationColumnsInfo.put(columnName, errors);
        }

        List<String> distributionErrors = distributionValidator.validate(column);
        if (CollectionUtils.isNotEmpty(distributionErrors)) {
            validationColumnsInfo.put(columnName, distributionErrors);
        }

        if (customType != null) {
            CustomTypeValidator customTypeValidator = customTypeValidatorsProvider.getByType(customType);
            if (customTypeValidator != null) {
                List<String> customTypeErrors = customTypeValidator.validate(column, rowsCount);
                if (CollectionUtils.isNotEmpty(customTypeErrors)) {
                    validationColumnsInfo.put(columnName, customTypeErrors);
                }
            }
        }
    }

    // Длина строки задаётся полем length, для обратной совместимости допускается поле end
    private boolean hasValidStringLength(ColumnDto column) {
        Integer length = column.getConfig().getLength();
        if (length != null) {
            return length > 0;
        }

        try {
            return Integer.parseInt(column.getConfig().getEnd().trim()) > 0;
        } catch (NumberFormatException | NullPointerException exception) {
            return false;
        }
    }

    @Override
    public GenerationStrategy getStrategy() {
        return GenerationStrategy.ALGORITHM;
    }
}
