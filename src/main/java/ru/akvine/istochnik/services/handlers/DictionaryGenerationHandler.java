package ru.akvine.istochnik.services.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.compozit.commons.utils.CollectionUtils;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.GenerationHandler;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.dto.GenerateColumn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DictionaryGenerationHandler implements GenerationHandler {
    private final ConverterConvertersProvider converterConvertersProvider;

    @Override
    public List<?> handle(GenerateColumn generateColumn) {
        List<Converter> converters = generateColumn.getConverters();
        List<String> dictionary = generateColumn.getConfig().getDictionaries().stream()
                .flatMap(Collection::stream)
                .toList();
        int size = generateColumn.getConfig().getSize();
        RangeType rangeType = generateColumn.getConfig().getRangeType();

        List<String> generatedValues = new ArrayList<>();
        while (generatedValues.size() != size) {
            if (rangeType == RangeType.RANDOM) {
                String element = CollectionUtils.getRandomElement(dictionary);
                generatedValues.add(element);
            } else {
                dictionary.forEach(element -> {
                    if (generatedValues.size() == size) {
                        return;
                    }
                    generatedValues.add(element);
                });
            }
        }

        return converterConvertersProvider.getByType(BaseType.STRING).apply(
                generatedValues,
                converters,
                generateColumn.getConfig().getRandomGenerator());
    }

    @Override
    public GenerationStrategy getStrategy() {
        return GenerationStrategy.DICTIONARY;
    }
}
