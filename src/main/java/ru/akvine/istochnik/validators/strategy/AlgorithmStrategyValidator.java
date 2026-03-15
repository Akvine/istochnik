package ru.akvine.istochnik.validators.strategy;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.istochnik.controllers.dto.validation.ValidationColumnsInfo;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.providers.BaseTypeValidatorsProvider;

import java.util.List;
import java.util.Objects;

@Component
public class AlgorithmStrategyValidator extends AbstractGenerationStrategyValidator {
    @Autowired
    public AlgorithmStrategyValidator(BaseTypeValidatorsProvider baseTypeValidatorsProvider) {
        super(baseTypeValidatorsProvider);
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

    @Override
    public GenerationStrategy getStrategy() {
        return GenerationStrategy.ALGORITHM;
    }
}
