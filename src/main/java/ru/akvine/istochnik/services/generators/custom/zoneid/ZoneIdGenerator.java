package ru.akvine.istochnik.services.generators.custom.zoneid;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.exceptions.GenerationException;
import ru.akvine.istochnik.services.generators.Generator;

@Service
public class ZoneIdGenerator implements Generator<String, ZoneIdGeneratorConfig> {
    // Сортировка нужна для воспроизводимости: порядок элементов в ZoneId.getAvailableZoneIds() не определён
    public static final List<String> ZONE_IDS =
            ZoneId.getAvailableZoneIds().stream().sorted().toList();

    @Override
    public List<String> generate(ZoneIdGeneratorConfig config) {
        if (config.isUnique()) {
            return generateUnique(config);
        }

        return generateNonUnique(config);
    }

    private List<String> generateUnique(ZoneIdGeneratorConfig config) {
        int size = config.getSize();
        // Если колонка может содержать null, то он выступает ещё одним уникальным значением
        List<String> candidates = new ArrayList<>(ZONE_IDS);
        if (!config.isNotNull()) {
            candidates.add(null);
        }

        if (size > candidates.size()) {
            String errorMessage = String.format(
                    "Rows count = [%s] greater than available unique zone ids = [%s]", size, candidates.size());
            throw new GenerationException(errorMessage);
        }

        if (config.getRangeType() != RangeType.SHIFT) {
            Collections.shuffle(candidates, config.getRandomGenerator());
        }

        return new ArrayList<>(candidates.subList(0, size));
    }

    private List<String> generateNonUnique(ZoneIdGeneratorConfig config) {
        int size = config.getSize();
        RandomGenerator randomGenerator = config.getRandomGenerator();
        // Для shift идентификаторы перебираются по алфавиту по кругу, иначе выбираются случайно
        boolean shift = config.getRangeType() == RangeType.SHIFT;

        List<String> generatedValues = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            if (!config.isNotNull() && randomGenerator.nextBoolean()) {
                generatedValues.add(null);
                continue;
            }

            int index = shift ? i % ZONE_IDS.size() : randomGenerator.nextInt(ZONE_IDS.size());
            generatedValues.add(ZONE_IDS.get(index));
        }

        return generatedValues;
    }
}
