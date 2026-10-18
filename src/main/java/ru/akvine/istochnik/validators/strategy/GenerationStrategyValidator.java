package ru.akvine.istochnik.validators.strategy;

import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.validation.ValidationColumnsInfo;
import ru.akvine.istochnik.enums.GenerationStrategy;

public interface GenerationStrategyValidator {
    void validate(ValidationColumnsInfo validationColumnsInfo, ColumnDto column, int rowsCount);

    GenerationStrategy getStrategy();
}
