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
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.dto.GenerateColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.random.RandomGenerator;

@Service
@RequiredArgsConstructor
public class FakerGenerationHandler implements GenerationHandler {
    private final FakerGeneratorServicesProvider fakerGeneratorServicesProvider;
    private final ConverterConvertersProvider converterConvertersProvider;
    private final UniqueValuesGenerator uniqueValuesGenerator;

    @Override
    public List<?> handle(GenerateColumn generateColumn) {
        Config config = generateColumn.getConfig();
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

        // По порядку топики чередуются по номеру строки, а случайно выбираются для каждого значения
        IntFunction<String> valueForRow = row -> {
            Topic topic = rangeType == RangeType.SHIFT
                    ? topics.get(row % topics.size())
                    : randomTopic(topics, randomGenerator);
            return fakerGeneratorServicesProvider.getByTopic(topic).generate(faker, config);
        };

        List<String> generatedValues;
        if (config.isUnique()) {
            generatedValues = uniqueValuesGenerator.generate(config, getStrategy(), valueForRow);
        } else {
            generatedValues = new ArrayList<>(size);
            for (int row = 0; row < size; ++row) {
                if (!notNull && randomGenerator.nextBoolean()) {
                    generatedValues.add(null);
                } else {
                    generatedValues.add(valueForRow.apply(row));
                }
            }
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
