package ru.akvine.istochnik.services.impl;

import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.RejectedExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import ru.akvine.compozit.commons.istochnik.ErrorResolveInfo;
import ru.akvine.compozit.commons.istochnik.ErrorResolvePolicy;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.config.async.TaskExecutor;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.exceptions.GenerationException;
import ru.akvine.istochnik.providers.GenerationHandlersProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.GeneratorFacade;
import ru.akvine.istochnik.services.dto.GenerateColumn;
import ru.akvine.istochnik.services.dto.GenerateData;
import ru.akvine.istochnik.services.dto.Table;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeneratorFacadeImpl implements GeneratorFacade {
    private final GenerationHandlersProvider generationHandlersProvider;
    private final ConverterConvertersProvider converterConvertersProvider;
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
        List<CompletableFuture<List<?>>> futures = new ArrayList<>(generateColumns.size());

        StopWatch watch = null;
        if (logTotalEnabled) {
            watch = new StopWatch();
            watch.start();
        }

        for (GenerateColumn generateColumn : generateColumns) {
            CompletableFuture<List<?>> future;
            try {
                future = CompletableFuture.supplyAsync(() -> generateColumn(generateColumn), taskExecutor.executor());
            } catch (RejectedExecutionException exception) {
                future = CompletableFuture.failedFuture(
                        new GenerationException("Generation executor is overloaded. Task was rejected", exception));
            }
            futures.add(future);
        }

        // Колонки добавляются в таблицу в порядке запроса и только из потока запроса
        for (int i = 0; i < generateColumns.size(); ++i) {
            GenerateColumn generateColumn = generateColumns.get(i);
            List<?> generatedValues;
            try {
                generatedValues = futures.get(i).join();
            } catch (CompletionException exception) {
                generatedValues = resolveError(generateColumn, size, exception.getCause());
            }

            table.addColumn(generateColumn.getName(), generatedValues);
        }

        if (logTotalEnabled) {
            watch.stop();
            log.info("Data generating completed successfully in [{}] seconds.", watch.getTotalTimeSeconds());
        }
        return table;
    }

    private List<?> generateColumn(GenerateColumn generateColumn) {
        GenerationStrategy strategy = generateColumn.getGenerationStrategy();

        StopWatch columnWatch = null;
        if (logColumnEnabled) {
            columnWatch = new StopWatch();
            columnWatch.start();
        }

        List<?> generatedValues =
                generationHandlersProvider.getByType(strategy).handle(generateColumn);

        if (logColumnEnabled) {
            columnWatch.stop();
            log.info(
                    "Data generating for column = [{}] completed successfully in [{}] seconds.",
                    generateColumn.getName(),
                    columnWatch.getTotalTimeSeconds());
        }

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

        if (generatedValues == null || generatedValues.size() != generateColumn.getConfig().getSize()) {
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
