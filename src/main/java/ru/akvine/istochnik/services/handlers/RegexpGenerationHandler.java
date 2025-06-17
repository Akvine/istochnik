package ru.akvine.istochnik.services.handlers;

import lombok.RequiredArgsConstructor;
import nl.flotsam.xeger.Xeger;
import org.springframework.stereotype.Service;
import ru.akvine.compozit.commons.utils.CollectionUtils;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.exceptions.DefaultException;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.GenerationHandler;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.dto.GenerateColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
                String regexp = CollectionUtils.getRandomElement(regexps);
                Xeger generator = new Xeger(regexp);
                String generated = generator.generate();
                generatedValues.add(generated);
            } else {
                regexps.forEach(regexp -> {
                    if (generatedValues.size() == size) {
                        return;
                    }
                    Xeger generator = new Xeger(regexp);
                    String generated = generator.generate();
                    generatedValues.add(generated);
                });
            }
        }

        return converterConvertersProvider.getByType(BaseType.STRING).apply(generatedValues, converters);
    }

    @Override
    public GenerationStrategy getStrategy() {
        return GenerationStrategy.REGEXP;
    }
}
