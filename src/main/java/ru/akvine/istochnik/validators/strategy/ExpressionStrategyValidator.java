package ru.akvine.istochnik.validators.strategy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.istochnik.controllers.dto.validation.ValidationColumnsInfo;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.exceptions.ExpressionDependencyException;
import ru.akvine.istochnik.exceptions.InvalidExpressionException;
import ru.akvine.istochnik.providers.BaseTypeValidatorsProvider;
import ru.akvine.istochnik.services.ExpressionColumnService;
import ru.akvine.istochnik.services.expression.ColumnExpression;
import ru.akvine.istochnik.validators.type.dto.ValidateAction;

@Component
public class ExpressionStrategyValidator extends AbstractGenerationStrategyValidator {
    private final ExpressionColumnService expressionColumnService;

    @Autowired
    public ExpressionStrategyValidator(
            BaseTypeValidatorsProvider baseTypeValidatorsProvider, ExpressionColumnService expressionColumnService) {
        super(baseTypeValidatorsProvider);
        this.expressionColumnService = expressionColumnService;
    }

    @Override
    public void validate(ValidationColumnsInfo validationColumnsInfo, ColumnDto column, int rowsCount) {
        String columnName = column.getName();

        if (!expressionColumnService.isEnabled()) {
            validationColumnsInfo.put(columnName, ErrorMessages.STRATEGY_DISABLED_ERROR);
            return;
        }

        ConfigDto config = column.getConfig();
        if (config == null) {
            validationColumnsInfo.put(columnName, ErrorMessages.CONFIG_IS_NULL_ERROR);
            return;
        }

        try {
            expressionColumnService.parse(config.getExpression());
        } catch (InvalidExpressionException exception) {
            validationColumnsInfo.put(columnName, exception.getMessage());
        }

        if (config.isUnique()) {
            validationColumnsInfo.put(columnName, ErrorMessages.UNIQUE_NOT_SUPPORTED_ERROR);
        }

        BaseType baseType = BaseType.from(column.getType());
        if (baseType == null) {
            validationColumnsInfo.put(columnName, ErrorMessages.TYPE_IS_NOT_BASE_ERROR);
        } else {
            validateConverters(validationColumnsInfo, column, baseType, rowsCount);
        }
    }

    /**
     * Проверяет ссылки вычисляемых колонок на другие колонки запроса: колонка существует, её имя не повторяется,
     * ссылки не образуют цикл. Выражения с ошибками пропускаются: о них сообщает проверка самой колонки
     */
    public void validateDependencies(ValidationColumnsInfo validationColumnsInfo, List<ColumnDto> columns) {
        if (!expressionColumnService.isEnabled()) {
            return;
        }

        Map<Integer, ColumnExpression> expressions = new LinkedHashMap<>();
        for (int i = 0; i < columns.size(); i++) {
            ColumnDto column = columns.get(i);
            if (!isExpressionColumn(column) || column.getConfig() == null) {
                continue;
            }

            try {
                expressions.put(
                        i, expressionColumnService.parse(column.getConfig().getExpression()));
            } catch (InvalidExpressionException ignored) {
            }
        }

        if (expressions.isEmpty()) {
            return;
        }

        List<String> columnNames = columns.stream().map(ColumnDto::getName).toList();
        try {
            expressionColumnService.plan(columnNames, expressions);
        } catch (ExpressionDependencyException exception) {
            exception
                    .getErrors()
                    .forEach((index, errors) ->
                            validationColumnsInfo.put(columnNames.get(index), new ArrayList<>(errors)));
        }
    }

    @Override
    public GenerationStrategy getStrategy() {
        return GenerationStrategy.EXPRESSION;
    }

    private void validateConverters(
            ValidationColumnsInfo validationColumnsInfo, ColumnDto column, BaseType baseType, int rowsCount) {
        if (CollectionUtils.isEmpty(column.getConverters())) {
            return;
        }

        if (baseType == BaseType.BOOLEAN) {
            validationColumnsInfo.put(column.getName(), ErrorMessages.BOOLEAN_CONVERTERS_NOT_SUPPORTED_ERROR);
            return;
        }

        // От проверки базового типа здесь нужна только проверка конвертеров. Диапазона у вычисляемой колонки нет,
        // поэтому вместо него передаётся заведомо корректный
        ValidateAction action = new ValidateAction()
                .setRowsCount(rowsCount)
                .setRangeType(RangeType.RANDOM.getType())
                .setStart("0")
                .setEnd("1")
                .setConverters(column.getConverters());

        List<String> errors = baseTypeValidatorsProvider.get(baseType).validate(column.getName(), action);
        if (CollectionUtils.isNotEmpty(errors)) {
            validationColumnsInfo.put(column.getName(), errors);
        }
    }

    private boolean isExpressionColumn(ColumnDto column) {
        return GenerationStrategy.EXPRESSION.getName().equalsIgnoreCase(column.getGenerationStrategy());
    }

    interface ErrorMessages {
        String STRATEGY_DISABLED_ERROR = "generation strategy [expression] is disabled by app settings";
        String CONFIG_IS_NULL_ERROR = "field [config] can't be null";
        String UNIQUE_NOT_SUPPORTED_ERROR = "field [unique] is not supported for generation strategy [expression]";
        String TYPE_IS_NOT_BASE_ERROR =
                "field [type] must be one of base types [int, double, str, bool] for generation strategy [expression]";
        String BOOLEAN_CONVERTERS_NOT_SUPPORTED_ERROR = "converters are not supported for type [bool]";
    }
}
