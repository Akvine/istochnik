package ru.akvine.istochnik.services.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import nl.flotsam.xeger.Xeger;
import org.springframework.stereotype.Service;
import ru.akvine.compozit.commons.utils.CollectionUtils;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.GenerationHandler;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.dto.GenerateColumn;

@Service
@RequiredArgsConstructor
public class RegexpGenerationHandler implements GenerationHandler {
    private final ConverterConvertersProvider converterConvertersProvider;

    @Override
    public List<?> handle(GenerateColumn generateColumn) {
        List<Converter> converters = generateColumn.getConverters();
        Set<String> regexps = generateColumn.getConfig().getRegexps();
        int size = generateColumn.getConfig().getSize();
        List<String> generatedValues = new ArrayList<>();
        RangeType rangeType = generateColumn.getConfig().getRangeType();

        while (generatedValues.size() != size) {
            if (rangeType == RangeType.RANDOM) {
                generateByRandomRangeType(generatedValues, generateColumn, regexps);
            } else {
                generateByShiftRangeType(generatedValues, generateColumn, regexps);
            }
        }

        return converterConvertersProvider
                .getByType(BaseType.STRING)
                .apply(generatedValues, converters, generateColumn.getConfig().getRandomGenerator());
    }

    private void generateByRandomRangeType(List<String> generatedValues,
                                           GenerateColumn generateColumn,
                                           Set<String> regexps) {
        if (generateColumn.getConfig().isNotNull()) {
            String regexp = CollectionUtils.getRandomElement(regexps);
            Xeger generator = new Xeger(regexp);
            String generated = generator.generate();
            generatedValues.add(generated);
        } else {
            boolean isNull = generateColumn.getConfig().getRandomGenerator().nextBoolean();
            if (isNull) {
                generatedValues.add(null);
            } else {
                String regexp = CollectionUtils.getRandomElement(regexps);
                Xeger generator = new Xeger(regexp);
                String generated = generator.generate();
                generatedValues.add(generated);
            }
        }
    }

    private void generateByShiftRangeType(List<String> generatedValues,
                                          GenerateColumn generateColumn,
                                          Set<String> regexps) {
        regexps.forEach(regexp -> {
            if (generatedValues.size() == generateColumn.getConfig().getSize()) {
                return;
            }

            if (generateColumn.getConfig().isNotNull()) {
                Xeger generator = new Xeger(regexp);
                String generated = generator.generate();
                generatedValues.add(generated);
            } else {
                boolean isNull = generateColumn.getConfig().getRandomGenerator().nextBoolean();
                if (isNull) {
                    generatedValues.add(null);
                } else {
                    Xeger generator = new Xeger(regexp);
                    String generated = generator.generate();
                    generatedValues.add(generated);
                }
            }
        });
    }

    @Override
    public GenerationStrategy getStrategy() {
        return GenerationStrategy.REGEXP;
    }
}
