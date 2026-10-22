package ru.akvine.istochnik.services.handlers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.random.RandomGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.exceptions.GenerationException;
import ru.akvine.istochnik.services.dto.Config;

/**
 * Генерирует колонку без повторов для стратегий, значения которых нельзя перечислить заранее: значение строится
 * случайно, а повтор отбрасывается и строится заново. Если колонка допускает null, он встречается не больше одного
 * раза, как и в остальных стратегиях
 */
@Component
public class UniqueValuesGenerator {
    // Сколько повторов подряд на каждое найденное значение означает, что значения закончились. Если ненайденным
    // осталось одно значение из N, оно выпадает в среднем раз за N попыток, и 20 * N повторов подряд случаются
    // с вероятностью около e^-20. Без этого правила колонка с малым набором значений исчерпала бы все попытки
    // max.generation.attempts на строку, а это миллионы вызовов генератора
    private static final int DUPLICATES_PER_FOUND_VALUE = 20;

    @Value("${max.generation.attempts}")
    private int maxGenerationAttempts;

    /**
     * @param valueForRow строит значение для строки с указанным номером. Номер нужен стратегиям, которые
     *     перебирают выражения или топики по порядку
     * @throws GenerationException если уникальных значений не хватает на все строки
     */
    public List<String> generate(Config config, GenerationStrategy strategy, IntFunction<String> valueForRow) {
        int size = config.getSize();
        RandomGenerator randomGenerator = config.getRandomGenerator();

        List<String> generatedValues = new ArrayList<>(size);
        Set<String> uniqueValues = new HashSet<>();
        boolean nullGenerated = false;

        long attempts = 0;
        long maxAttempts = (long) size * maxGenerationAttempts;
        long duplicatesInRow = 0;
        while (generatedValues.size() < size) {
            if (!config.isNotNull() && !nullGenerated && randomGenerator.nextBoolean()) {
                nullGenerated = true;
                generatedValues.add(null);
                continue;
            }

            attempts++;
            String value = valueForRow.apply(generatedValues.size());
            if (uniqueValues.add(value)) {
                generatedValues.add(value);
                duplicatesInRow = 0;
                continue;
            }

            duplicatesInRow++;
            long maxDuplicatesInRow = maxGenerationAttempts + (long) DUPLICATES_PER_FOUND_VALUE * uniqueValues.size();
            if (attempts > maxAttempts || duplicatesInRow > maxDuplicatesInRow) {
                String message = String.format(
                        "Unique values of [%s] strategy run out: only [%s] unique values were generated for [%s] "
                                + "rows in [%s] attempts. Disable field \"unique\": \"true\" or reduce rows count",
                        strategy.getName(), uniqueValues.size(), size, attempts);
                throw new GenerationException(message);
            }
        }

        return generatedValues;
    }
}
