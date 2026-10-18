package ru.akvine.istochnik.services.impl;

import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import ru.akvine.istochnik.controllers.dto.ErrorResolveInfo;
import ru.akvine.istochnik.enums.ErrorResolvePolicy;
import ru.akvine.istochnik.utils.Asserts;
import ru.akvine.istochnik.config.async.TaskExecutor;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.exceptions.GenerationException;
import ru.akvine.istochnik.providers.GenerationHandlersProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.ExpressionColumnService;
import ru.akvine.istochnik.services.GeneratorFacade;
import ru.akvine.istochnik.services.dto.GenerateColumn;
import ru.akvine.istochnik.services.dto.GenerateData;
import ru.akvine.istochnik.services.dto.Table;
import ru.akvine.istochnik.services.expression.ColumnExpression;
import ru.akvine.istochnik.services.expression.SourceColumn;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeneratorFacadeImpl implements GeneratorFacade {
    private final GenerationHandlersProvider generationHandlersProvider;
    private final ConverterConvertersProvider converterConvertersProvider;
    private final ExpressionColumnService expressionColumnService;
    private final TaskExecutor taskExecutor;

    @Value("${log.total.generation.data.benchmark.enabled}")
    private boolean logTotalEnabled;

    @Value("${log.column.generation.data.benchmark.enabled}")
    private boolean logColumnEnabled;

    @Override
    public Table generate(GenerateData generateData) {
        Asserts.isNotNull(generateData, "generateData is null");

        int size = generateData.getSize();
        String tableName = generateData.getTableName();
        Table table = new Table(size, tableName);
        List<GenerateColumn> generateColumns = generateData.getGenerateColumns();
        // Значения колонок по индексам колонок в запросе
        List<List<?>> generatedColumns = new ArrayList<>(Collections.nCopies(generateColumns.size(), null));

        StopWatch watch = null;
        if (logTotalEnabled) {
            watch = new StopWatch();
            watch.start();
        }

        // Обычные колонки друг от друга не зависят и генерируются одновременно
        Map<Integer, CompletableFuture<List<?>>> futures = new LinkedHashMap<>();
        for (int i = 0; i < generateColumns.size(); ++i) {
            GenerateColumn generateColumn = generateColumns.get(i);
            if (generateColumn.getGenerationStrategy() != GenerationStrategy.EXPRESSION) {
                futures.put(i, submit(() -> generateColumn(generateColumn)));
            }
        }
        collect(futures, generateColumns, generatedColumns);

        // Вычисляемым колонкам нужны готовые значения других колонок, поэтому они считаются после обычных
        if (futures.size() < generateColumns.size()) {
            calculateExpressionColumns(generateColumns, generatedColumns);
        }

        // Колонки добавляются в таблицу в порядке запроса и только из потока запроса
        for (int i = 0; i < generateColumns.size(); ++i) {
            table.addColumn(generateColumns.get(i).getName(), generatedColumns.get(i));
        }

        if (logTotalEnabled) {
            watch.stop();
            log.info("Data generating completed successfully in [{}] seconds.", watch.getTotalTimeSeconds());
        }

        return table;
    }

    private List<?> generateColumn(GenerateColumn generateColumn) {
        GenerationStrategy strategy = generateColumn.getGenerationStrategy();

        List<?> generatedValues = measure(
                generateColumn,
                () -> generationHandlersProvider.getByType(strategy).handle(generateColumn));

        if (strategy == GenerationStrategy.ALGORITHM
                && generateColumn.isConvertToString()
                && generateColumn.getBaseType() != BaseType.STRING
                && CollectionUtils.isNotEmpty(generateColumn.getPostConverters())) {
            List<String> converted =
                    generatedValues.stream().map(String::valueOf).toList();
            generatedValues = converterConvertersProvider
                    .getByType(BaseType.STRING)
                    .apply(
                            converted,
                            generateColumn.getPostConverters(),
                            generateColumn.getConfig().getRandomGenerator());
        }

        return verifySize(generateColumn, generatedValues);
    }

    private void calculateExpressionColumns(List<GenerateColumn> generateColumns, List<List<?>> generatedColumns) {
        if (!expressionColumnService.isEnabled()) {
            throw new GenerationException("Generation strategy [expression] is disabled by app settings");
        }

        List<String> columnNames =
                generateColumns.stream().map(GenerateColumn::getName).toList();
        Map<Integer, ColumnExpression> expressions = new LinkedHashMap<>();
        for (int i = 0; i < generateColumns.size(); ++i) {
            GenerateColumn generateColumn = generateColumns.get(i);
            if (generateColumn.getGenerationStrategy() == GenerationStrategy.EXPRESSION) {
                expressions.put(
                        i,
                        expressionColumnService.parse(generateColumn.getConfig().getExpression()));
            }
        }

        // Колонки одного уровня зависят только от уже готовых колонок, поэтому вычисляются одновременно
        for (List<Integer> level : expressionColumnService.plan(columnNames, expressions)) {
            Map<Integer, CompletableFuture<List<?>>> futures = new LinkedHashMap<>();
            for (Integer index : level) {
                GenerateColumn generateColumn = generateColumns.get(index);
                ColumnExpression expression = expressions.get(index);

                Map<String, SourceColumn> sources = new HashMap<>();
                for (String name : expression.dependencies()) {
                    int sourceIndex = columnNames.indexOf(name);
                    sources.put(
                            name,
                            new SourceColumn(generateColumns.get(sourceIndex), generatedColumns.get(sourceIndex)));
                }

                futures.put(index, submit(() -> calculateColumn(generateColumn, expression, sources)));
            }
            collect(futures, generateColumns, generatedColumns);
        }
    }

    private List<?> calculateColumn(
            GenerateColumn generateColumn, ColumnExpression expression, Map<String, SourceColumn> sources) {
        List<?> calculatedValues =
                measure(generateColumn, () -> expressionColumnService.calculate(generateColumn, expression, sources));
        return verifySize(generateColumn, calculatedValues);
    }

    private CompletableFuture<List<?>> submit(Supplier<List<?>> task) {
        try {
            return CompletableFuture.supplyAsync(task, taskExecutor.executor());
        } catch (RejectedExecutionException exception) {
            return CompletableFuture.failedFuture(
                    new GenerationException("Generation executor is overloaded. Task was rejected", exception));
        }
    }

    // Результаты забираются в порядке запроса: если ошибок несколько, сообщается о первой по порядку колонке
    private void collect(
            Map<Integer, CompletableFuture<List<?>>> futures,
            List<GenerateColumn> generateColumns,
            List<List<?>> generatedColumns) {
        futures.forEach((index, future) -> {
            GenerateColumn generateColumn = generateColumns.get(index);
            List<?> generatedValues;
            try {
                generatedValues = future.join();
            } catch (CompletionException exception) {
                generatedValues =
                        resolveError(generateColumn, generateColumn.getConfig().getSize(), exception.getCause());
            }
            generatedColumns.set(index, generatedValues);
        });
    }

    private List<?> measure(GenerateColumn generateColumn, Supplier<List<?>> generation) {
        if (!logColumnEnabled) {
            return generation.get();
        }

        StopWatch columnWatch = new StopWatch();
        columnWatch.start();
        List<?> generatedValues = generation.get();
        columnWatch.stop();
        log.info(
                "Data generating for column = [{}] completed successfully in [{}] seconds.",
                generateColumn.getName(),
                columnWatch.getTotalTimeSeconds());

        return generatedValues;
    }

    private List<?> verifySize(GenerateColumn generateColumn, List<?> generatedValues) {
        if (generatedValues == null
                || generatedValues.size() != generateColumn.getConfig().getSize()) {
            String errorMessage = String.format(
                    "generated values count = [%s] is not equal to rows count = [%s]",
                    generatedValues == null ? null : generatedValues.size(),
                    generateColumn.getConfig().getSize());
            throw new GenerationException(errorMessage);
        }

        return generatedValues;
    }

    private List<?> resolveError(GenerateColumn generateColumn, int size, Throwable cause) {
        ErrorResolveInfo info = generateColumn.getErrorResolveInfo();
        if (info != null && info.getPolicy() == ErrorResolvePolicy.IGNORE) {
            log.warn(
                    "For column with name = [{}] has been occurred error: [{}]. Default value is used",
                    generateColumn.getName(),
                    cause.toString());
            return Collections.nCopies(size, info.getDefaultValue());
        }

        String errorMessage = String.format(
                "Error while generate column with name = [%s]: %s",
                generateColumn.getName(), cause.getMessage() == null ? cause.toString() : cause.getMessage());
        throw new GenerationException(errorMessage, cause);
    }

    @PreDestroy
    public void destroy() {
        taskExecutor.executor().shutdown();
    }
}
