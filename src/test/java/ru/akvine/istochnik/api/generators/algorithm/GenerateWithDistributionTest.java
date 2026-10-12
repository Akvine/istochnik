package ru.akvine.istochnik.api.generators.algorithm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.hamcrest.Matchers.containsString;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.DistributionDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.api.ApiBaseTest;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;

@DisplayName("Distributions of random values tests")
public class GenerateWithDistributionTest extends ApiBaseTest {
    private static final int SIZE = 10_000;

    @Test
    @DisplayName("Normal distribution of integers - values are gathered around the mean")
    void successful_generate_normal_integers() {
        List<Long> values = generate(
                BaseType.INTEGER.getValue(),
                random("0", "101")
                        .setDistribution(distribution("normal").setMean("50").setStddev("10")),
                Long::parseLong);

        assertThat(values).hasSize(SIZE).allMatch(value -> value >= 0 && value <= 100);
        assertThat(average(values)).isCloseTo(50, within(0.5));
        // В пределах одного стандартного отклонения от среднего лежит около 70 % значений, а не 20 %, как при
        // равномерном распределении
        assertThat(share(values, value -> value >= 40 && value <= 60)).isCloseTo(0.706, within(0.03));
        assertThat(share(values, value -> value < 20 || value > 80)).isLessThan(0.01);
    }

    @Test
    @DisplayName("Normal distribution without parameters - mean in the middle of the range")
    void successful_generate_normal_datetime_with_defaults() {
        LocalDateTime start = LocalDateTime.parse("2025-01-01T00:00:00");
        LocalDateTime end = LocalDateTime.parse("2025-01-13T00:00:00");
        List<LocalDateTime> values = generate(
                CustomType.DATETIME.getName(),
                random("2025-01-01 00:00:00", "2025-01-13 00:00:00").setDistribution(distribution("normal")),
                LocalDateTime::parse);

        assertThat(values).hasSize(SIZE).allMatch(value -> !value.isBefore(start) && value.isBefore(end));
        // Стандартное отклонение по умолчанию - шестая часть диапазона, то есть двое суток
        LocalDateTime middle = LocalDateTime.parse("2025-01-07T00:00:00");
        assertThat(share(values, value -> !value.isBefore(middle.minusDays(2)) && value.isBefore(middle.plusDays(2))))
                .isCloseTo(0.683, within(0.03));
        assertThat(share(values, value -> value.isBefore(middle))).isCloseTo(0.5, within(0.03));
    }

    @Test
    @DisplayName("Normal distribution of decimals - scale of values is kept")
    void successful_generate_normal_decimals() {
        List<BigDecimal> values = generate(
                CustomType.DECIMAL.getName(),
                random("0.00", "100.00")
                        .setDistribution(distribution("normal").setMean("30").setStddev("5.5")),
                BigDecimal::new);

        assertThat(values).hasSize(SIZE).allMatch(value -> value.scale() == 2 && value.signum() >= 0);
        assertThat(values).allMatch(value -> value.compareTo(new BigDecimal("100.00")) < 0);
        assertThat(average(values)).isCloseTo(30, within(0.3));
        assertThat(share(values, value -> Math.abs(value.doubleValue() - 30) < 5.5))
                .isCloseTo(0.683, within(0.03));
    }

    @Test
    @DisplayName("Exponential distribution of doubles - values are dense at the start of the range")
    void successful_generate_exponential_doubles() {
        List<Double> values = generate(
                BaseType.DOUBLE.getValue(),
                random("0", "100").setDistribution(distribution("exponential").setStddev("10")),
                Double::parseDouble);

        assertThat(values).hasSize(SIZE).allMatch(value -> value >= 0 && value < 100);
        assertThat(average(values)).isCloseTo(10, within(0.5));
        // Не дальше среднего расстояния от пика лежит 63 % значений
        assertThat(share(values, value -> value < 10)).isCloseTo(0.632, within(0.03));
    }

    @Test
    @DisplayName("Exponential distribution of dates with the peak at the end - recent dates are more frequent")
    void successful_generate_exponential_dates_with_peak_at_end() {
        LocalDate start = LocalDate.parse("2025-01-01");
        LocalDate end = LocalDate.parse("2026-01-01");
        List<LocalDate> values = generate(
                CustomType.DATE.getName(),
                random("2025-01-01", "2026-01-01")
                        .setDistribution(
                                distribution("exponential").setStddev("30").setPeak("end")),
                LocalDate::parse);

        assertThat(values).hasSize(SIZE).allMatch(value -> !value.isBefore(start) && value.isBefore(end));
        assertThat(share(values, value -> !value.isBefore(end.minusDays(30)))).isCloseTo(0.632, within(0.03));
        assertThat(share(values, value -> value.isBefore(LocalDate.parse("2025-07-01"))))
                .isLessThan(0.01);
    }

    @Test
    @DisplayName("Exponential distribution of zoned date times - values stay inside the range")
    void successful_generate_exponential_zoned_datetime() {
        ZonedDateTime start = ZonedDateTime.parse("2025-01-01T00:00:00+03:00[Europe/Moscow]");
        ZonedDateTime end = ZonedDateTime.parse("2025-01-11T00:00:00+03:00[Europe/Moscow]");
        List<ZonedDateTime> values = generate(
                CustomType.ZONED_DATETIME.getName(),
                random("2025-01-01T00:00:00+03:00[Europe/Moscow]", "2025-01-11T00:00:00+03:00[Europe/Moscow]")
                        .setDistribution(distribution("exponential")),
                ZonedDateTime::parse);

        assertThat(values).hasSize(SIZE).allMatch(value -> !value.isBefore(start) && value.isBefore(end));
        // Среднее расстояние от пика по умолчанию - пятая часть диапазона, то есть двое суток
        assertThat(share(values, value -> value.isBefore(start.plusDays(2)))).isCloseTo(0.636, within(0.03));
    }

    @Test
    @DisplayName("Zipf distribution of integers - a few values at the start take most of the rows")
    void successful_generate_zipf_integers() {
        List<Long> values = generate(
                BaseType.INTEGER.getValue(),
                random("1", "1001").setDistribution(distribution("zipf")),
                Long::parseLong);

        assertThat(values).hasSize(SIZE).allMatch(value -> value >= 1 && value <= 1000);
        Map<Long, Long> counts =
                values.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        // Значение с номером k встречается в k раз реже первого
        assertThat((double) counts.get(1L) / counts.get(2L)).isCloseTo(2, within(0.4));
        assertThat((double) counts.get(1L) / counts.get(4L)).isCloseTo(4, within(0.9));
        // На первые десять значений из тысячи приходится 39 % строк, а на вторую половину диапазона - 9 %
        assertThat(share(values, value -> value <= 10)).isCloseTo(0.391, within(0.03));
        assertThat(share(values, value -> value > 500)).isCloseTo(0.093, within(0.02));
    }

    @Test
    @DisplayName("Zipf distribution of time with the peak at the end - the most frequent value is the last one")
    void successful_generate_zipf_time_with_peak_at_end() {
        List<LocalTime> values = generate(
                CustomType.TIME.getName(),
                random("10:00:00", "10:01:00")
                        .setDistribution(distribution("zipf").setExponent("2").setPeak("end")),
                LocalTime::parse);

        assertThat(values)
                .hasSize(SIZE)
                .allMatch(value ->
                        !value.isBefore(LocalTime.parse("10:00:00")) && value.isBefore(LocalTime.parse("10:01:00")));
        // При показателе 2 на самое частое значение приходится 61 % строк, на второе - вчетверо меньше
        assertThat(share(values, value -> value.equals(LocalTime.parse("10:00:59"))))
                .isCloseTo(0.615, within(0.03));
        assertThat(share(values, value -> value.equals(LocalTime.parse("10:00:58"))))
                .isCloseTo(0.154, within(0.03));
    }

    @Test
    @DisplayName("Distribution is combined with null values")
    void successful_generate_with_nulls() {
        List<String> values = generate(
                BaseType.INTEGER.getValue(),
                random("0", "101")
                        .setNotNull(false)
                        .setDistribution(distribution("normal").setMean("50").setStddev("5")),
                Function.identity());

        assertThat(values).hasSize(SIZE);
        // В CSV null записывается пустым значением
        assertThat(share(values, String::isEmpty)).isCloseTo(0.5, within(0.03));
        assertThat(values.stream().filter(value -> !value.isEmpty()).map(Long::parseLong))
                .allMatch(value -> value >= 0 && value <= 100)
                .anyMatch(value -> value == 50);
    }

    @Test
    @DisplayName("Uniform distribution set explicitly gives the same values as no distribution")
    void uniform_distribution_is_default() {
        byte[] withoutDistribution = sendGenerateRequest(request(BaseType.INTEGER.getValue(), random("0", "1000")));
        byte[] uniform = sendGenerateRequest(
                request(BaseType.INTEGER.getValue(), random("0", "1000").setDistribution(distribution("uniform"))));
        byte[] normal = sendGenerateRequest(
                request(BaseType.INTEGER.getValue(), random("0", "1000").setDistribution(distribution("normal"))));

        assertThat(uniform).isEqualTo(withoutDistribution);
        assertThat(normal).isNotEqualTo(withoutDistribution);
    }

    @Test
    @DisplayName("Invalid distribution - validation error")
    void fail_generate_with_invalid_distribution() {
        String intType = BaseType.INTEGER.getValue();

        sendInvalid(
                request(intType, random("0", "10").setDistribution(distribution("gauss"))),
                "field [distribution.type] must be one of [uniform, normal, exponential, zipf]");
        sendInvalid(
                request(
                        intType,
                        random("0", "10").setDistribution(distribution("normal").setMean("11"))),
                "field [distribution.mean] must be between field [start] and field [end]");
        sendInvalid(
                request(
                        intType,
                        random("0", "10").setDistribution(distribution("normal").setStddev("0"))),
                "field [distribution.stddev] must be a number greater than 0");
        sendInvalid(
                request(
                        intType,
                        random("0", "10")
                                .setDistribution(distribution("exponential").setMean("5"))),
                "field [distribution.mean] is not used by [exponential] distribution. Supported fields: [peak, stddev]");
        sendInvalid(
                request(
                        intType,
                        random("0", "10").setDistribution(distribution("zipf").setExponent("11"))),
                "field [distribution.exponent] must be a number greater than 0 and not greater than 10");
        sendInvalid(
                request(BaseType.DOUBLE.getValue(), random("0", "10").setDistribution(distribution("zipf"))),
                "[zipf] distribution is not supported for type [double]");
        sendInvalid(
                request(
                        CustomType.DATE.getName(),
                        random("2025-01-01", "soon").setDistribution(distribution("normal"))),
                "field [distribution] requires valid fields [start] and [end]");

        ConfigDto shift = new ConfigDto()
                .setRangeType(RangeType.SHIFT.getType())
                .setStart("1")
                .setEnd("100000")
                .setStep("1")
                .setDistribution(distribution("normal"));
        sendInvalid(request(intType, shift), "field [distribution] is used only when field [rangeType] is [random]");

        ConfigDto string = new ConfigDto().setLength(5).setDistribution(distribution("normal"));
        sendInvalid(
                request(BaseType.STRING.getValue(), string),
                "field [distribution] is not supported for type [str]. "
                        + "Supported types: [int, double, decimal, date, datetime, time, zoned_datetime]");
    }

    private ConfigDto random(String start, String end) {
        return new ConfigDto()
                .setRangeType(RangeType.RANDOM.getType())
                .setStart(start)
                .setEnd(end)
                .setNotNull(true);
    }

    private DistributionDto distribution(String type) {
        return new DistributionDto().setType(type);
    }

    private GenerateTableRequest request(String type, ConfigDto config) {
        ColumnDto column = new ColumnDto()
                .setName("column")
                .setType(type)
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(config);

        return new GenerateTableRequest()
                .setSize(SIZE)
                .setFileType(FileType.CSV.name())
                .setColumns(List.of(column));
    }

    private <T> List<T> generate(String type, ConfigDto config, Function<String, T> parser) {
        return convert(BaseType.STRING, sendGenerateRequest(request(type, config))).stream()
                .map(String.class::cast)
                .map(parser)
                .toList();
    }

    private double average(List<? extends Number> values) {
        return values.stream().mapToDouble(Number::doubleValue).average().orElseThrow();
    }

    private <T> double share(List<T> values, java.util.function.Predicate<T> condition) {
        return (double) values.stream().filter(condition).count() / values.size();
    }

    private void sendInvalid(GenerateTableRequest request, String expectedMessage) {
        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(containsString(expectedMessage));
    }
}
