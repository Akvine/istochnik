package ru.akvine.istochnik.services.limits;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.config.async.TaskExecutor;
import ru.akvine.istochnik.config.limits.GenerationLimitsCalculationProperties;

/**
 * Лимиты запроса на генерацию, которые действуют в приложении. Определяются один раз при запуске:
 * берутся из настроек или рассчитываются по ресурсам машины, если включён расчёт
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GenerationLimitsService {
    private static final long BYTES_IN_MEGABYTE = 1024 * 1024;

    private final GenerationLimitsCalculationProperties calculationProperties;
    private final GenerationLimitsCalculator generationLimitsCalculator;
    private final TaskExecutor taskExecutor;

    @Value("${max.generation.rows.size}")
    private int maxRowsSize;

    @Value("${max.generation.columns.size}")
    private int maxColumnsSize;

    @Value("${max.generation.cells.size}")
    private long maxCellsSize;

    @Getter
    private GenerationLimits limits;

    @PostConstruct
    public void init() {
        if (!calculationProperties.isEnabled()) {
            // Лимит ячеек в настройках один на все форматы файлов
            limits = new GenerationLimits(maxRowsSize, maxColumnsSize, maxCellsSize, maxCellsSize);
            log.info("Generation limits are taken from settings: {}", limits);
            return;
        }

        long maxHeapBytes = Runtime.getRuntime().maxMemory();
        ThreadPoolExecutor executor = taskExecutor.executor();
        // Пул ещё пуст, поэтому свободна вся очередь
        int executorCapacity =
                executor.getMaximumPoolSize() + executor.getQueue().remainingCapacity();

        limits = generationLimitsCalculator.calculate(maxHeapBytes, executorCapacity, calculationProperties);
        log.info(
                "Generation limits are calculated by machine resources: max heap = [{}] MB, CPU cores = [{}], "
                        + "generation threads = [{}], executor capacity = [{}], users = [{}]. {}",
                maxHeapBytes / BYTES_IN_MEGABYTE,
                Runtime.getRuntime().availableProcessors(),
                executor.getMaximumPoolSize(),
                executorCapacity,
                calculationProperties.getUsers(),
                limits);
    }
}
