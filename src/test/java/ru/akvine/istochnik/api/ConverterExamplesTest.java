package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.akvine.istochnik.controllers.dto.converters.ConverterDto;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.ConverterType;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.providers.BaseTypeValidatorsProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.ConverterService;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.validators.type.dto.ValidateAction;

@DisplayName("Converter examples tests")
public class ConverterExamplesTest extends ApiBaseTest {
    // Случайный конвертер выдаёт результат из примера не с первой попытки. Генератор с постоянным seed
    // делает перебор воспроизводимым
    private static final int MAX_ATTEMPTS = 200_000;

    @Autowired
    private ConverterConvertersProvider converterServicesProvider;

    @Autowired
    private BaseTypeValidatorsProvider baseTypeValidatorsProvider;

    @Test
    @DisplayName("Example of every converter passes request validation")
    void every_example_is_valid() {
        for (BaseType baseType : BaseType.values()) {
            for (ConverterType converterType : baseType.getSupportedConverterType()) {
                ConverterType.Example example = converterType.getExample();
                ConverterDto converter = new ConverterDto()
                        .setName(converterType.getName())
                        .setArguments(example.arguments().toArray())
                        .setProbability(probability(example));
                ValidateAction action = new ValidateAction()
                        .setStart("1")
                        .setEnd("10")
                        .setRangeType(RangeType.RANDOM.getType())
                        .setRowsCount(example.values().size())
                        .setConverters(List.of(converter));

                assertThat(baseTypeValidatorsProvider.get(baseType).validate("column", action))
                        .as("%s for type %s", converterType.getName(), baseType.getValue())
                        .isEmpty();
            }
        }
    }

    @Test
    @DisplayName("Converter turns example values into example results")
    void every_example_is_produced_by_converter() {
        for (BaseType baseType : BaseType.values()) {
            for (ConverterType converterType : baseType.getSupportedConverterType()) {
                // У типа без конвертеров нет и сервиса конвертеров, поэтому сервис запрашивается только здесь
                ConverterService converterService = converterServicesProvider.getByType(baseType);
                ConverterType.Example example = converterType.getExample();
                Converter converter = new Converter()
                        .setName(converterType)
                        .setArguments(example.arguments().toArray())
                        .setProbability(probability(example));
                List<?> values = typed(baseType, example.values());
                List<?> results = typed(baseType, example.results());

                RandomGenerator randomGenerator = new Random(42);
                boolean produced = IntStream.range(0, MAX_ATTEMPTS).anyMatch(attempt -> converterService
                        .apply(values, List.of(converter), randomGenerator)
                        .equals(results));

                assertThat(produced)
                        .as(
                                "%s for type %s turns %s into %s",
                                converterType.getName(), baseType.getValue(), values, results)
                        .isTrue();
            }
        }
    }

    private int probability(ConverterType.Example example) {
        return example.probability() == null ? 100 : example.probability();
    }

    // Значения примера приводятся к тому типу, с которым работают конвертеры колонки
    private List<?> typed(BaseType baseType, List<?> values) {
        return values.stream()
                .map(value -> value == null
                        ? null
                        : switch (baseType) {
                                // Дробное число в примере для int - ошибка примера, а не повод отбросить дробную часть
                            case INTEGER -> new BigDecimal(value.toString()).longValueExact();
                            case DOUBLE -> ((Number) value).doubleValue();
                            default -> String.valueOf(value);
                        })
                .toList();
    }
}
