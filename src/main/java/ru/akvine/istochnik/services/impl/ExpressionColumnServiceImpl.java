package ru.akvine.istochnik.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.config.expression.ExpressionProperties;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.exceptions.ExpressionDependencyException;
import ru.akvine.istochnik.exceptions.GenerationException;
import ru.akvine.istochnik.exceptions.InvalidExpressionException;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.ExpressionColumnService;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.GenerateColumn;
import ru.akvine.istochnik.services.expression.ColumnExpression;
import ru.akvine.istochnik.services.expression.ColumnExpressionParser;
import ru.akvine.istochnik.services.expression.DependencyGraph;
import ru.akvine.istochnik.services.expression.ExpressionFunctions;
import ru.akvine.istochnik.services.expression.SourceColumn;

@Service
@RequiredArgsConstructor
public class ExpressionColumnServiceImpl implements ExpressionColumnService {
    private final ExpressionProperties expressionProperties;
    private final ColumnExpressionParser columnExpressionParser;
    private final ConverterConvertersProvider converterConvertersProvider;

    @Override
    public boolean isEnabled() {
        return expressionProperties.isEnabled();
    }

    @Override
    public ColumnExpression parse(String expression) {
        if (StringUtils.isBlank(expression)) {
            throw new InvalidExpressionException("field [expression] can't be blank");
        }

        int maxLength = expressionProperties.getMaxLength();
        if (expression.length() > maxLength) {
            throw new InvalidExpressionException("field [expression] can't be longer than max = [" + maxLength + "]");
        }

        return columnExpressionParser.parse(expression);
    }

    @Override
    public List<List<Integer>> plan(List<String> columnNames, Map<Integer, ColumnExpression> expressions) {
        Map<String, List<Integer>> indexesByName = new HashMap<>();
        for (int i = 0; i < columnNames.size(); i++) {
            indexesByName
                    .computeIfAbsent(columnNames.get(i), name -> new ArrayList<>())
                    .add(i);
        }

        Map<Integer, List<String>> errors = new TreeMap<>();
        // Вычисляемая колонка и вычисляемые колонки, от которых она зависит. Обычные колонки в граф не входят:
        // к началу вычислений они уже готовы
        Map<Integer, Set<Integer>> dependencies = new LinkedHashMap<>();
        expressions.forEach((index, expression) -> {
            Set<Integer> columnDependencies = new LinkedHashSet<>();
            for (String name : expression.dependencies()) {
                List<Integer> indexes = indexesByName.getOrDefault(name, List.of());
                if (indexes.isEmpty()) {
                    addError(errors, index, "expression references unknown column [" + name + "]");
                } else if (indexes.size() > 1) {
                    addError(
                            errors,
                            index,
                            String.format(
                                    "expression references column [%s], but [%s] columns have this name",
                                    name, indexes.size()));
                } else if (indexes.getFirst().equals(index)) {
                    addError(errors, index, "expression references its own column [" + name + "]");
                } else if (expressions.containsKey(indexes.getFirst())) {
                    columnDependencies.add(indexes.getFirst());
                }
            }
            dependencies.put(index, columnDependencies);
        });

        List<Integer> cycle = DependencyGraph.findCycle(dependencies);
        if (!cycle.isEmpty()) {
            String path = cycle.stream().map(columnNames::get).collect(Collectors.joining(" -> "));
            cycle.stream()
                    .distinct()
                    .forEach(
                            index -> addError(errors, index, "expression is part of cyclic dependency [" + path + "]"));
        }

        if (!errors.isEmpty()) {
            throw new ExpressionDependencyException(errors);
        }

        return DependencyGraph.levels(dependencies);
    }

    @Override
    public List<?> calculate(GenerateColumn column, ColumnExpression expression, Map<String, SourceColumn> sources) {
        Config config = column.getConfig();
        int size = config.getSize();
        BaseType type = column.getBaseType();

        EvaluationContext context = buildContext();
        // Одна и та же карта переиспользуется для всех строк
        Map<String, Object> row = new HashMap<>();
        if (!expression.rowKeys().isEmpty()) {
            context.setVariable(ColumnExpression.ROW_VARIABLE, row);
        }

        List<Object> values = new ArrayList<>(size);
        for (int rowIndex = 0; rowIndex < size; rowIndex++) {
            boolean hasNull = false;
            for (String name : expression.variables()) {
                Object value = sources.get(name).valueAt(rowIndex);
                hasNull |= value == null;
                context.setVariable(name, value);
            }
            for (String name : expression.rowKeys()) {
                Object value = sources.get(name).valueAt(rowIndex);
                hasNull |= value == null;
                row.put(name, value);
            }

            // Как в SQL: значение, вычисленное по null, тоже null. Если колонка объявлена как notNull,
            // выражение получает null как есть и должно обработать его само
            if (hasNull && !config.isNotNull()) {
                values.add(null);
            } else {
                values.add(evaluate(expression, context, type, config.isNotNull(), rowIndex));
            }
        }

        if (CollectionUtils.isEmpty(column.getConverters())) {
            return values;
        }
        return converterConvertersProvider
                .getByType(type)
                .apply(values, column.getConverters(), config.getRandomGenerator());
    }

    // Контекст без доступа к типам, конструкторам, бинам, методам и свойствам объектов:
    // выражению доступны только переменные и функции
    private EvaluationContext buildContext() {
        EvaluationContext context = SimpleEvaluationContext.forPropertyAccessors()
                .withAssignmentDisabled()
                .build();
        ColumnExpressionParser.FUNCTIONS.forEach(context::setVariable);
        return context;
    }

    private Object evaluate(
            ColumnExpression expression, EvaluationContext context, BaseType type, boolean notNull, int rowIndex) {
        Object result;
        try {
            result = expression.expression().getValue(context);
        } catch (RuntimeException exception) {
            String message = exception.getMessage() == null ? exception.toString() : exception.getMessage();
            throw rowError(rowIndex, message, exception);
        }

        Object value;
        try {
            value = convert(result, type);
        } catch (IllegalArgumentException | ArithmeticException exception) {
            String message = String.format("result [%s] can't be converted to type [%s]", result, type.getValue());
            throw rowError(rowIndex, message, exception);
        }

        if (value == null && notNull) {
            throw rowError(rowIndex, "expression returned null, but field [notNull] is true", null);
        }

        int maxResultLength = expressionProperties.getMaxResultLength();
        if (value instanceof String text && text.length() > maxResultLength) {
            throw rowError(rowIndex, "result can't be longer than max = [" + maxResultLength + "] characters", null);
        }

        return value;
    }

    // Строки файла нумеруются с единицы
    private GenerationException rowError(int rowIndex, String message, Throwable cause) {
        return new GenerationException(
                String.format("can't calculate expression for row [%s]: %s", rowIndex + 1, message), cause);
    }

    private Object convert(Object value, BaseType type) {
        return switch (type) {
            case INTEGER -> ExpressionFunctions.toInt(value);
            case DOUBLE -> ExpressionFunctions.toDouble(value);
            case STRING -> ExpressionFunctions.toStr(value);
            case BOOLEAN -> toBoolean(value);
        };
    }

    private Boolean toBoolean(Object value) {
        if (value == null || value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String text
                && (Boolean.TRUE.toString().equalsIgnoreCase(text)
                        || Boolean.FALSE.toString().equalsIgnoreCase(text))) {
            return Boolean.parseBoolean(text);
        }

        throw new IllegalArgumentException("value [" + value + "] is not a boolean");
    }

    private void addError(Map<Integer, List<String>> errors, Integer index, String message) {
        errors.computeIfAbsent(index, key -> new ArrayList<>()).add(message);
    }
}
