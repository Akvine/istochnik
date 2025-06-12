package ru.akvine.istochnik.services.impl;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.config.async.TaskExecutor;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.providers.GenerationHandlersProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.GeneratorFacade;
import ru.akvine.istochnik.services.dto.GenerateColumn;
import ru.akvine.istochnik.services.dto.GenerateData;
import ru.akvine.istochnik.services.dto.Table;

import java.util.ArrayList;
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

    @Override
    public Table generate(GenerateData generateData) {
        Asserts.isNotNull(generateData, "generateData is null");

        Table table = new Table(generateData.getSize());
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (GenerateColumn generateColumn : generateData.getGenerateColumns()) {
            try {
                CompletableFuture<Void> result = CompletableFuture.runAsync(
                        () -> {
                            GenerationStrategy strategy = generateColumn.getGenerationStrategy();
                            List<?> generatedValues = generationHandlersProvider.getByType(strategy).handle(generateColumn);

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
                );
                futures.add(result);
            } catch (RejectedExecutionException exception) {
                log.info("Executor [{}] is full. Task was rejected");
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return table;
    }

    @PreDestroy
    public void destroy() {
        taskExecutor.executor().shutdown();
    }
}
