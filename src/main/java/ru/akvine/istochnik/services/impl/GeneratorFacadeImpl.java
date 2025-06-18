package ru.akvine.istochnik.services.impl;

import jakarta.annotation.PreDestroy;
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
import ru.akvine.istochnik.exceptions.DefaultException;
import ru.akvine.istochnik.providers.GenerationHandlersProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.GeneratorFacade;
import ru.akvine.istochnik.services.dto.GenerateColumn;
import ru.akvine.istochnik.services.dto.GenerateData;
import ru.akvine.istochnik.services.dto.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;

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
        Table table = new Table(size);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        StopWatch watch = null;
        if (logTotalEnabled) {
            watch = new StopWatch();
            watch.start();
        }

        for (GenerateColumn generateColumn : generateData.getGenerateColumns()) {
            try {
                CompletableFuture<Void> result = CompletableFuture.runAsync(
                        () -> {
                            GenerationStrategy strategy = generateColumn.getGenerationStrategy();

                            StopWatch columnWatch = null;
                            if (logColumnEnabled) {
                                columnWatch = new StopWatch();
                                columnWatch.start();
                            }

                            List<?> generatedValues = generationHandlersProvider.getByType(strategy).handle(generateColumn);

                            if (logColumnEnabled) {
                                columnWatch.stop();
                                log.info("Data generating for column = [{}] completed successfully in [{}] seconds.",
                                        generateColumn.getName(), columnWatch.getTotalTimeSeconds());
                            }

                            if (strategy == GenerationStrategy.ALGORITHM &&
                                    generateColumn.isConvertToString() &&
                                    generateColumn.getBaseType() != BaseType.STRING &&
                                    CollectionUtils.isNotEmpty(generateColumn.getPostConverters())) {
                                List<String> converted = generatedValues.stream().map(String::valueOf).toList();
                                generatedValues = converterConvertersProvider
                                        .getByType(BaseType.STRING)
                                        .apply(converted, generateColumn.getPostConverters());
                            }

                            table.addColumn(generateColumn.getName(), generatedValues);
                        },
                        taskExecutor.executor()
                ).exceptionally(exception -> {
                    if (exception.getCause() instanceof DefaultException) {
                        ErrorResolveInfo info = ((DefaultException) exception.getCause()).getErrorResolveInfo();
                        if (info.getPolicy() == ErrorResolvePolicy.INTERRUPT) {
                            throw new RuntimeException(exception);
                        }

                        String defaultValue = info.getDefaultValue();
                        table.addColumn(
                                generateColumn.getName(),
                                Collections.nCopies(size, defaultValue));
                    }

                    log.info("For column with name = [{}] has been occurred error: [{}]", generateColumn.getName(), exception.getMessage());
                    return null;
                });
                futures.add(result);
            } catch (RejectedExecutionException exception) {
                log.info("Executor [{}] is full. Task was rejected");
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        if (logTotalEnabled) {
            watch.stop();
            log.info("Data generating completed successfully in [{}] seconds.", watch.getTotalTimeSeconds());
        }
        return table;
    }

    @PreDestroy
    public void destroy() {
        taskExecutor.executor().shutdown();
    }
}
