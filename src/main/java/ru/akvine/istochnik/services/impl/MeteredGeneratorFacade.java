package ru.akvine.istochnik.services.impl;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.GeneratorFacade;
import ru.akvine.istochnik.services.dto.GenerateData;
import ru.akvine.istochnik.services.dto.Table;

/**
 * Считает метрики генерации: длительность и количество сгенерированных строк. Оборачивает основную реализацию
 * и создаётся только при включённых метриках, поэтому запросы по HTTP и из очереди учитываются одинаково,
 * а при выключенных метриках генерация работает как прежде
 */
@Service
@Primary
@ConditionalOnProperty(name = "metrics.enabled", havingValue = "true")
@RequiredArgsConstructor
public class MeteredGeneratorFacade implements GeneratorFacade {
    private static final String GENERATION_TIMER_NAME = "istochnik.generation";
    private static final String GENERATED_ROWS_COUNTER_NAME = "istochnik.generation.rows";

    private static final String FILE_TYPE_TAG = "file.type";
    private static final String OUTCOME_TAG = "outcome";
    private static final String OUTCOME_SUCCESS = "success";
    private static final String OUTCOME_ERROR = "error";

    private final GeneratorFacadeImpl generatorFacade;
    private final MeterRegistry meterRegistry;

    @Override
    public Table generate(GenerateData generateData) {
        String fileType = generateData.getFileType().getExtension();
        Timer.Sample sample = Timer.start(meterRegistry);
        String outcome = OUTCOME_ERROR;
        try {
            Table table = generatorFacade.generate(generateData);
            outcome = OUTCOME_SUCCESS;
            meterRegistry
                    .counter(GENERATED_ROWS_COUNTER_NAME, FILE_TYPE_TAG, fileType)
                    .increment(generateData.getSize());
            return table;
        } finally {
            sample.stop(meterRegistry.timer(GENERATION_TIMER_NAME, FILE_TYPE_TAG, fileType, OUTCOME_TAG, outcome));
        }
    }
}
