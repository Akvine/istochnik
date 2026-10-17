package ru.akvine.istochnik.services.limits;

import org.springframework.stereotype.Component;
import ru.akvine.istochnik.config.limits.GenerationLimitsCalculationProperties;

/**
 * Рассчитывает лимиты запроса одного пользователя по ресурсам машины.
 * Ячейки ограничены памятью: таблица и файл целиком лежат в куче. Колонки ограничены пулом потоков генерации:
 * каждая колонка - отдельная задача, и задачи сверх потоков и очереди пула запрос отправить не сможет.
 * Строки отдельно не ограничиваются: таблица из одной колонки может занять все доступные ячейки
 */
@Component
public class GenerationLimitsCalculator {

    /**
     * @param maxHeapBytes максимальный размер кучи
     * @param executorCapacity сколько задач принимает пул генерации: его потоки и места в очереди
     * @param settings параметры расчёта
     */
    public GenerationLimits calculate(
            long maxHeapBytes, int executorCapacity, GenerationLimitsCalculationProperties settings) {
        int users = settings.getUsers();

        long memoryPerUser = (long) (maxHeapBytes * settings.getMemoryShare() / users);
        long cells = Math.max(1, memoryPerUser / settings.getBytesPerCell());
        long xlsxCells = Math.max(1, memoryPerUser / settings.getXlsxBytesPerCell());
        int columns = (int) Math.min(Math.max(1, executorCapacity / users), cells);
        int rows = (int) Math.min(cells, Integer.MAX_VALUE);

        return new GenerationLimits(rows, columns, cells, xlsxCells);
    }
}
