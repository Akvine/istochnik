package ru.akvine.istochnik.services.handlers;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.akvine.compozit.commons.utils.CollectionUtils;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.providers.FakerGeneratorServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.GenerationHandler;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.dto.GenerateColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.random.RandomGenerator;

@Service
@RequiredArgsConstructor
public class FakerGenerationHandler implements GenerationHandler {
    private final FakerGeneratorServicesProvider fakerGeneratorServicesProvider;
    private final ConverterConvertersProvider converterConvertersProvider;

    @Value("${test.randomizer.enabled}")
    private boolean testRandomizerEnabled;

    @Override
    public List<?> handle(GenerateColumn generateColumn) {
        List<Converter> converters = generateColumn.getConverters();
        List<Topic> topics = generateColumn.getConfig().getTopics();
        int size = generateColumn.getConfig().getSize();
        String language = generateColumn.getConfig().getLang();
        RangeType rangeType = generateColumn.getConfig().getRangeType();
        boolean notNull = generateColumn.getConfig().isNotNull();

        RandomGenerator randomGenerator = generateColumn.getConfig().getRandomGenerator();
        Faker faker;
        if (StringUtils.isBlank(language)) {
            if (testRandomizerEnabled) {
                faker = new Faker((Random) randomGenerator);
            } else {
                faker = new Faker();
            }
        } else {
            if (testRandomizerEnabled) {
                faker = new Faker(
                        new Locale(language),
                        (Random) randomGenerator
                );
            } else {
                faker = new Faker(new Locale(language));
            }
        }

        List<String> generatedValues = new ArrayList<>();
        int iterationCount = 0;
        while (generatedValues.size() != size) {
            if (rangeType == RangeType.SHIFT) {
                if (notNull) {
                    Topic currentTopic = topics.get(iterationCount % topics.size());
                    generatedValues.add(fakerGeneratorServicesProvider.getByTopic(currentTopic).generate(faker));
                } else {
                    boolean isNull = randomGenerator.nextBoolean();
                    if (isNull) {
                        generatedValues.add(null);
                    } else {
                        Topic currentTopic = topics.get(iterationCount % topics.size());
                        generatedValues.add(fakerGeneratorServicesProvider.getByTopic(currentTopic).generate(faker));
                    }
                }
            } else {
                if (notNull) {
                    Topic topic = CollectionUtils.getRandomElement(topics);
                    generatedValues.add(fakerGeneratorServicesProvider.getByTopic(topic).generate(faker));
                } else {
                    boolean isNull = randomGenerator.nextBoolean();
                    if (isNull) {
                        generatedValues.add(null);
                    } else {
                        Topic topic = CollectionUtils.getRandomElement(topics);
                        generatedValues.add(fakerGeneratorServicesProvider.getByTopic(topic).generate(faker));
                    }
                }
            }

            iterationCount++;
        }

        return converterConvertersProvider.getByType(BaseType.STRING).apply(
                generatedValues,
                converters,
                generateColumn.getConfig().getRandomGenerator());
    }

    @Override
    public GenerationStrategy getStrategy() {
        return GenerationStrategy.FAKER;
    }
}
