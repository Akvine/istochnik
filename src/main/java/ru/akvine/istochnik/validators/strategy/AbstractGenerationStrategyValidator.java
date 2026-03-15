package ru.akvine.istochnik.validators.strategy;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.istochnik.providers.BaseTypeValidatorsProvider;
import ru.akvine.istochnik.validators.type.dto.ValidateAction;

@RequiredArgsConstructor
public abstract class AbstractGenerationStrategyValidator implements GenerationStrategyValidator {
    protected final BaseTypeValidatorsProvider baseTypeValidatorsProvider;

    protected ValidateAction buildValidateAction(int rowsCount, ColumnDto columnDto) {
        ValidateAction action = new ValidateAction()
                .setRowsCount(rowsCount)
                .setStart(columnDto.getConfig().getStart())
                .setEnd(columnDto.getConfig().getEnd())
                .setStep(columnDto.getConfig().getStep())
                .setRangeType(columnDto.getConfig().getRangeType());

        if (CollectionUtils.isNotEmpty(columnDto.getConverters())) {
            action.setConverters(columnDto.getConverters());
        }

        return action;
    }
}
