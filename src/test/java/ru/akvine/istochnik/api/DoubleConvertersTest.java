package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.ConverterDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.ConverterType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;

@DisplayName("Double converters tests")
public class DoubleConvertersTest extends ApiBaseTest {
    private static final int SIZE = 8;
    private static final double PRECISION = 1e-9;

    // Исходные значения - последовательность 0.15, 0.25, 0.35...: все числа между 0 и 1,
    // поэтому к ним применимы и логарифмы, и обратные тригонометрические функции
    private static final ConfigDto SOURCE = new ConfigDto()
            .setRangeType(RangeType.SHIFT.getType())
            .setStart("0.15")
            .setEnd("100")
            .setStep("0.1");

    @Test
    @DisplayName("Apply converter with one value in and one value out - result is calculated for every value")
    void successful_apply_value_converters() {
        Map<ConverterDto, DoubleUnaryOperator> expected = new LinkedHashMap<>();
        expected.put(converter(ConverterType.ABS), Math::abs);
        expected.put(converter(ConverterType.NEGATIVE), value -> -value);
        expected.put(converter(ConverterType.PLUS, 2.5), value -> value + 2.5);
        expected.put(converter(ConverterType.MINUS, 0.5), value -> value - 0.5);
        expected.put(converter(ConverterType.MULTIPLY, -4), value -> value * -4);
        expected.put(converter(ConverterType.DIVIDE, 4), value -> value / 4);
        expected.put(converter(ConverterType.POW, 3), value -> Math.pow(value, 3));
        expected.put(converter(ConverterType.CEIL), Math::ceil);
        expected.put(converter(ConverterType.FLOOR), Math::floor);
        expected.put(converter(ConverterType.SIN), Math::sin);
        expected.put(converter(ConverterType.COS), Math::cos);
        expected.put(converter(ConverterType.TAN), Math::tan);
        expected.put(converter(ConverterType.COT), value -> 1 / Math.tan(value));
        expected.put(converter(ConverterType.ARCSIN), Math::asin);
        expected.put(converter(ConverterType.ARCCOS), Math::acos);
        expected.put(converter(ConverterType.ARCTAN), Math::atan);
        expected.put(converter(ConverterType.EXP), Math::exp);
        // Аргумент логарифмов обязателен, но в вычислении не участвует
        expected.put(converter(ConverterType.LOG, 1), Math::log);
        expected.put(converter(ConverterType.LOG10, 1), Math::log10);

        List<Double> source = generate(List.of());
        expected.forEach((converter, function) -> {
            List<Double> result = generate(List.of(converter));

            assertThat(result).as(converter.getName()).hasSameSizeAs(source);
            for (int index = 0; index < source.size(); index++) {
                assertThat(result.get(index))
                        .as("%s(%s)", converter.getName(), source.get(index))
                        .isCloseTo(function.applyAsDouble(source.get(index)), within(PRECISION));
            }
        });
    }

    @Test
    @DisplayName("Apply ROUND converter - value is rounded to the given count of fraction digits")
    void successful_apply_round() {
        ConfigDto config = new ConfigDto()
                .setRangeType(RangeType.SHIFT.getType())
                .setStart("0.234")
                .setEnd("100")
                .setStep("1.111");

        // 0.234, 1.345, 2.456, 3.567
        assertThat(generate(config, List.of(converter(ConverterType.ROUND, 1)))).startsWith(0.2, 1.3, 2.5, 3.6);
        assertThat(generate(config, List.of(converter(ConverterType.ROUND, 0)))).startsWith(0.0, 1.0, 2.0, 4.0);
    }

    @Test
    @DisplayName("Apply ZSCORE and MIN_MAX_SCALING converters - column values are normalized")
    void successful_apply_normalization() {
        List<Double> source = generate(List.of());
        double min = source.get(0);
        double max = source.get(source.size() - 1);

        List<Double> scaled = generate(List.of(converter(ConverterType.MINMAXSCALING)));
        for (int index = 0; index < source.size(); index++) {
            assertThat(scaled.get(index)).isCloseTo((source.get(index) - min) / (max - min), within(PRECISION));
        }

        // После стандартизации среднее равно нулю, а порядок значений сохраняется
        List<Double> standardized = generate(List.of(converter(ConverterType.ZSCORE)));
        double mean =
                standardized.stream().mapToDouble(Double::doubleValue).average().orElseThrow();
        assertThat(mean).isCloseTo(0, within(PRECISION));
        assertThat(standardized).isSorted();
        assertThat(standardized.get(0)).isNegative();
        assertThat(standardized.get(standardized.size() - 1)).isPositive();
    }

    @Test
    @DisplayName("Apply SORT, SHUFFLE and TO_NULL converters - whole column is changed")
    void successful_apply_column_converters() {
        List<Double> source = generate(List.of());
        List<Double> descending = new ArrayList<>(source);
        descending.sort(Comparator.reverseOrder());

        assertThat(generate(List.of(converter(ConverterType.SORT_DESC)))).isEqualTo(descending);
        assertThat(generate(List.of(converter(ConverterType.SORT_DESC), converter(ConverterType.SORT_ASC))))
                .isEqualTo(source);

        List<Double> shuffled = generate(List.of(converter(ConverterType.SHUFFLE)));
        assertThat(shuffled).containsExactlyInAnyOrderElementsOf(source).isNotEqualTo(source);

        // Пустое значение в CSV соответствует null
        GenerateTableRequest request = request(SOURCE, List.of(converter(ConverterType.TO_NULL)));
        assertThat(convert(BaseType.STRING, sendGenerateRequest(request)))
                .hasSize(SIZE)
                .allMatch(value -> String.valueOf(value).isEmpty());
    }

    @Test
    @DisplayName("Apply several converters - they are applied in the order of the request")
    void successful_apply_converters_chain() {
        List<Double> source = generate(List.of());

        List<Double> result = generate(List.of(
                converter(ConverterType.PLUS, 1),
                converter(ConverterType.MULTIPLY, 10),
                converter(ConverterType.FLOOR)));

        for (int index = 0; index < source.size(); index++) {
            assertThat(result.get(index)).isEqualTo(Math.floor((source.get(index) + 1) * 10));
        }
    }

    private List<Double> generate(List<ConverterDto> converters) {
        return generate(SOURCE, converters);
    }

    // Значения разбираются как строки: общий разбор дробных чисел округляет их до трёх знаков,
    // а здесь результат сравнивается с точным
    private List<Double> generate(ConfigDto config, List<ConverterDto> converters) {
        return convert(BaseType.STRING, sendGenerateRequest(request(config, converters))).stream()
                .map(value -> Double.parseDouble(String.valueOf(value)))
                .toList();
    }

    private GenerateTableRequest request(ConfigDto config, List<ConverterDto> converters) {
        ColumnDto column = new ColumnDto()
                .setName("column")
                .setType(BaseType.DOUBLE.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(config)
                .setConverters(converters);

        return new GenerateTableRequest()
                .setSize(SIZE)
                .setFileType(FileType.CSV.name())
                .setColumns(List.of(column));
    }

    private ConverterDto converter(ConverterType type, Object... arguments) {
        return new ConverterDto().setName(type.getName()).setArguments(arguments);
    }
}
