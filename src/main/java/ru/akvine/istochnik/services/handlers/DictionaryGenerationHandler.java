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
                generateByRandomRange(generatedValues, generateColumn, dictionary);
            } else {
                generateByShiftRange(generatedValues, generateColumn, dictionary);
            }
        }

        return converterConvertersProvider
                .getByType(BaseType.STRING)
                .apply(generatedValues, converters, generateColumn.getConfig().getRandomGenerator());
    }

    private void generateByShiftRange(List<String> generatedValues,
                                      GenerateColumn generateColumn,
                                      List<String> dictionary) {
        dictionary.forEach(element -> {
            if (generatedValues.size() == generateColumn.getConfig().getSize()) {
                return;
            }

            if (generateColumn.getConfig().isNotNull()) {
                generatedValues.add(element);
            } else {
                boolean isNull = generateColumn.getConfig().getRandomGenerator().nextBoolean();
                if (isNull) {
                    if (generateColumn.getConfig().isUnique()) {
                        if (!generatedValues.contains(null)) {
                            generatedValues.add(null);
                        } else {
                            generatedValues.add(element);
                        }
                    } else {
                        generatedValues.add(null);
                    }
                } else {
                    generatedValues.add(element);
                }
            }
        });
    }

    private void generateByRandomRange(List<String> generatedValues,
                                       GenerateColumn generateColumn,
                                       List<String> dictionary) {
        if (generateColumn.getConfig().isUnique()) {
            if (generateColumn.getConfig().isNotNull()) {
                String element = CollectionUtils.getRandomElement(dictionary);

                if (generatedValues.contains(element)) {
                    return;
                }

                generatedValues.add(element);
            } else {
                boolean isNull = generateColumn.getConfig().getRandomGenerator().nextBoolean();
                if (isNull && !generatedValues.contains(null)) {
                    generatedValues.add(null);
                    return;
                }

                String element = CollectionUtils.getRandomElement(dictionary);
                if (generatedValues.contains(element)) {
                    return;
                }

                generatedValues.add(element);
            }
        } else {
            if (generateColumn.getConfig().isNotNull()) {
                String element = CollectionUtils.getRandomElement(dictionary);
                generatedValues.add(element);
            } else {
                boolean isNull = generateColumn.getConfig().getRandomGenerator().nextBoolean();
                if (isNull) {
                    generatedValues.add(null);
                } else {
                    String element = CollectionUtils.getRandomElement(dictionary);
                    generatedValues.add(element);
                }
            }
        }
    }

    @Override
    public GenerationStrategy getStrategy() {
        return GenerationStrategy.DICTIONARY;
    }
}
