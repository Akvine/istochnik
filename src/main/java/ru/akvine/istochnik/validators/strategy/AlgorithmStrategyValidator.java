package ru.akvine.istochnik.validators.strategy;

import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.istochnik.controllers.dto.validation.ValidationColumnsInfo;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.providers.BaseTypeValidatorsProvider;

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
