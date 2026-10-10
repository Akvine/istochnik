package ru.akvine.istochnik.services.handlers;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.exceptions.GenerationException;
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


    @Override
    public List<?> handle(GenerateColumn generateColumn) {
        List<Converter> converters = generateColumn.getConverters();
        List<Topic> topics = generateColumn.getConfig().getTopics();
        int size = generateColumn.getConfig().getSize();
        String language = generateColumn.getConfig().getLang();
        RangeType rangeType = generateColumn.getConfig().getRangeType();
        boolean notNull = generateColumn.getConfig().isNotNull();

        if (topics == null || topics.isEmpty()) {
            throw new GenerationException("topics can't be empty");
        }

        RandomGenerator randomGenerator = generateColumn.getConfig().getRandomGenerator();
        Faker faker;
        if (StringUtils.isBlank(language)) {
            faker = new Faker((Random) randomGenerator);
        } else {
            faker = new Faker(new Locale(language), (Random) randomGenerator);
        }

        List<String> generatedValues = new ArrayList<>();
        int iterationCount = 0;
        // TODO: добавить поддерку unique
        while (generatedValues.size() != size) {
            if (rangeType == RangeType.SHIFT) {
                if (notNull) {
                    Topic currentTopic = topics.get(iterationCount % topics.size());
                    generatedValues.add(fakerGeneratorServicesProvider
                            .getByTopic(currentTopic)
                            .generate(faker));
                } else {
                    boolean isNull = randomGenerator.nextBoolean();
                    if (isNull) {
                        generatedValues.add(null);
                    } else {
                        Topic currentTopic = topics.get(iterationCount % topics.size());
                        generatedValues.add(fakerGeneratorServicesProvider
                                .getByTopic(currentTopic)
                                .generate(faker));
                    }
                }
            } else {
                if (notNull) {
                    Topic topic = randomTopic(topics, randomGenerator);
                    generatedValues.add(
                            fakerGeneratorServicesProvider.getByTopic(topic).generate(faker));
                } else {
                    boolean isNull = randomGenerator.nextBoolean();
                    if (isNull) {
                        generatedValues.add(null);
                    } else {
                        Topic topic = randomTopic(topics, randomGenerator);
                        generatedValues.add(
                                fakerGeneratorServicesProvider.getByTopic(topic).generate(faker));
                    }
                }
            }

            iterationCount++;
        }

        return converterConvertersProvider
                .getByType(BaseType.STRING)
                .apply(generatedValues, converters, generateColumn.getConfig().getRandomGenerator());
    }

    // Для единственного топика генератор не расходуется, чтобы не менять последовательность значений по seed
    private Topic randomTopic(List<Topic> topics, RandomGenerator randomGenerator) {
        return topics.size() == 1 ? topics.getFirst() : topics.get(randomGenerator.nextInt(topics.size()));
    }

    @Override
    public GenerationStrategy getStrategy() {
        return GenerationStrategy.FAKER;
    }
}
