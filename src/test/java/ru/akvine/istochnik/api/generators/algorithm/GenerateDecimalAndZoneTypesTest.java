package ru.akvine.istochnik.api.generators.algorithm;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.ConverterDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.api.ApiBaseTest;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.ConverterType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.custom.zoneid.ZoneIdGenerator;

@DisplayName("Decimal, zoned datetime and zone id types tests")
public class GenerateDecimalAndZoneTypesTest extends ApiBaseTest {

    @Test
    @DisplayName("Generate random decimals - scale is taken from start and end")
    void successful_generate_random_decimal_values() {
        List<String> result = generate(
                CustomType.DECIMAL,
                50,
                new ConfigDto()
                        .setNotNull(true)
                        .setRangeType(RangeType.RANDOM.getType())
                        .setStart("10.00")
                        .setEnd("20.00"));

        assertThat(result).hasSize(50);
        assertThat(result).allSatisfy(value -> {
            BigDecimal decimal = new BigDecimal(value);
            assertThat(decimal.scale()).isEqualTo(2);
            assertThat(decimal).isGreaterThanOrEqualTo(new BigDecimal("10.00"));
            assertThat(decimal).isLessThan(new BigDecimal("20.00"));
        });
    }

    @Test
    @DisplayName("Generate shifted decimals without end - values are exact")
    void successful_generate_shifted_decimal_values() {
        List<String> result = generate(
                CustomType.DECIMAL,
                5,
                new ConfigDto()
                        .setRangeType(RangeType.SHIFT.getType())
                        .setStart("0.10")
                        .setStep("0.10"));

        assertThat(result).containsExactly("0.10", "0.20", "0.30", "0.40", "0.50");
    }

    @Test
    @DisplayName("Generate shifted decimals with end - scale is the largest of start, end and step")
    void successful_generate_shifted_decimal_values_with_end() {
        List<String> result = generate(
                CustomType.DECIMAL,
                4,
                new ConfigDto()
                        .setRangeType(RangeType.SHIFT.getType())
                        .setStart("1.5")
                        .setEnd("100")
                        .setStep("0.25"));

        assertThat(result).containsExactly("1.50", "1.75", "2.00", "2.25");
    }

    @Test
    @DisplayName("Generate unique decimals - all values of a small range are used")
    void successful_generate_unique_decimal_values() {
        List<String> result = generate(
                CustomType.DECIMAL,
                10,
                new ConfigDto()
                        .setNotNull(true)
                        .setUnique(true)
                        .setRangeType(RangeType.RANDOM.getType())
                        .setStart("0.0")
                        .setEnd("1.0"));

        assertThat(result)
                .containsExactlyInAnyOrder("0.0", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9");
    }

    @Test
    @DisplayName("Generate decimals with large scale - no exponential notation")
    void successful_generate_decimal_values_with_large_scale() {
        List<String> result = generate(
                CustomType.DECIMAL,
                20,
                new ConfigDto()
                        .setNotNull(true)
                        .setRangeType(RangeType.RANDOM.getType())
                        .setStart("0.000000000000000001")
                        .setEnd("0.000000000000000009"));

        assertThat(result).allSatisfy(value -> assertThat(value).matches("0\\.0{17}[1-8]"));
    }

    @Test
    @DisplayName("Generate decimals with string converter")
    void successful_generate_decimal_values_with_converter() {
        List<String> result = generate(new ColumnDto()
                .setName("column")
                .setType(CustomType.DECIMAL.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.SHIFT.getType())
                        .setStart("99.90")
                        .setStep("0.10"))
                .setConverters(List.of(new ConverterDto()
                        .setName(ConverterType.ADD_AFTER.getName())
                        .setArguments(new Object[] {" RUB"}))),
                2);

        assertThat(result).containsExactly("99.90 RUB", "100.00 RUB");
    }

    @Test
    @DisplayName("Decimal with invalid config - validation error")
    void fail_generate_decimal_values_with_invalid_config() {
        assertThat(generateInvalid(
                        CustomType.DECIMAL,
                        10,
                        new ConfigDto()
                                .setRangeType(RangeType.RANDOM.getType())
                                .setStart("abc")
                                .setEnd("1E+1000")))
                .contains("field [start] is not a decimal")
                .contains("field [end] is not a decimal");

        assertThat(generateInvalid(
                        CustomType.DECIMAL,
                        11,
                        new ConfigDto()
                                .setNotNull(true)
                                .setUnique(true)
                                .setRangeType(RangeType.RANDOM.getType())
                                .setStart("0.0")
                                .setEnd("1.0")))
                .contains("Rows count = [11] greater than available unique values = [10]");

        assertThat(generateInvalid(
                        CustomType.DECIMAL,
                        10,
                        new ConfigDto()
                                .setRangeType(RangeType.SHIFT.getType())
                                .setStart("0")
                                .setEnd("1")
                                .setStep("0.5")))
                .contains("Possible generated rows less than size");
    }

    @Test
    @DisplayName("Generate random zoned datetimes - zone is taken from start")
    void successful_generate_random_zoned_datetime_values() {
        ZonedDateTime start = ZonedDateTime.parse("2025-01-02T14:00:00+03:00[Europe/Moscow]");
        ZonedDateTime end = ZonedDateTime.parse("2025-11-02T11:00:00Z");

        List<String> result = generate(
                CustomType.ZONED_DATETIME,
                50,
                new ConfigDto()
                        .setNotNull(true)
                        .setRangeType(RangeType.RANDOM.getType())
                        .setStart("2025-01-02T14:00:00+03:00[Europe/Moscow]")
                        .setEnd("2025-11-02T11:00:00Z"));

        assertThat(result).hasSize(50);
        assertThat(result).allSatisfy(value -> {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(value);
            assertThat(zonedDateTime.getZone()).isEqualTo(ZoneId.of("Europe/Moscow"));
            assertThat(zonedDateTime.toInstant()).isAfterOrEqualTo(start.toInstant());
            assertThat(zonedDateTime.toInstant()).isBefore(end.toInstant());
        });
    }

    @Test
    @DisplayName("Generate shifted zoned datetimes - daylight saving time gap is handled")
    void successful_generate_shifted_zoned_datetime_values() {
        List<String> result = generate(
                CustomType.ZONED_DATETIME,
                4,
                new ConfigDto().setRangeType(RangeType.SHIFT.getType()).setStart("2025-03-30 01:59:58 Europe/Berlin"));

        assertThat(result)
                .containsExactly(
                        "2025-03-30T01:59:58+01:00[Europe/Berlin]",
                        "2025-03-30T01:59:59+01:00[Europe/Berlin]",
                        "2025-03-30T03:00:00+02:00[Europe/Berlin]",
                        "2025-03-30T03:00:01+02:00[Europe/Berlin]");
    }

    @Test
    @DisplayName("Generate shifted zoned datetimes with offset - output has no zone region")
    void successful_generate_shifted_zoned_datetime_values_with_offset() {
        List<String> result = generate(
                CustomType.ZONED_DATETIME,
                3,
                new ConfigDto()
                        .setRangeType(RangeType.SHIFT.getType())
                        .setStart("2025-01-02T14:00:00+03:00")
                        .setEnd("2025-01-02T16:00:00+03:00")
                        .setStep("3600"));

        assertThat(result)
                .containsExactly(
                        "2025-01-02T14:00:00+03:00", "2025-01-02T15:00:00+03:00", "2025-01-02T16:00:00+03:00");
    }

    @Test
    @DisplayName("Generate zoned datetimes with format converter")
    void successful_generate_zoned_datetime_values_with_format_converter() {
        List<String> result = generate(new ColumnDto()
                .setName("column")
                .setType(CustomType.ZONED_DATETIME.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.SHIFT.getType())
                        .setStart("2025-01-02 14:00:00 Europe/Moscow")
                        .setStep("60"))
                .setConverters(List.of(new ConverterDto()
                        .setName(ConverterType.DATE_FORMAT.getName())
                        .setArguments(new Object[] {"dd.MM.yyyy HH:mm:ss XXX"}))),
                2);

        assertThat(result).containsExactly("02.01.2025 14:00:00 +03:00", "02.01.2025 14:01:00 +03:00");
    }

    @Test
    @DisplayName("Zoned datetime with invalid config - validation error")
    void fail_generate_zoned_datetime_values_with_invalid_config() {
        assertThat(generateInvalid(
                        CustomType.ZONED_DATETIME,
                        10,
                        new ConfigDto()
                                .setRangeType(RangeType.RANDOM.getType())
                                .setStart("2025-01-02 14:00:00")
                                .setEnd("2025-11-02T14:00:00+03:00")))
                .contains("field [start] is not a zoned datetime");

        assertThat(generateInvalid(
                        CustomType.ZONED_DATETIME,
                        10,
                        new ConfigDto()
                                .setRangeType(RangeType.SHIFT.getType())
                                .setStart("2025-01-02T14:00:00+03:00")
                                .setEnd("2025-01-02T14:00:05+03:00")))
                .contains("Possible generated rows less than size");

        assertThat(generateInvalid(
                        CustomType.ZONED_DATETIME, 10, new ConfigDto().setStart("2025-01-02T14:00:00+03:00")))
                .contains("field [rangeType] can't be blank");
    }

    @Test
    @DisplayName("Generate random zone ids - all values are valid")
    void successful_generate_random_zone_id_values() {
        List<String> result = generate(CustomType.ZONE_ID, 50, new ConfigDto().setNotNull(true));

        assertThat(result).hasSize(50);
        assertThat(result).allSatisfy(value -> assertThat(ZoneId.of(value)).isNotNull());
    }

    @Test
    @DisplayName("Generate unique zone ids")
    void successful_generate_unique_zone_id_values() {
        List<String> result =
                generate(CustomType.ZONE_ID, 100, new ConfigDto().setNotNull(true).setUnique(true));

        assertThat(new HashSet<>(result)).hasSize(100);
    }

    @Test
    @DisplayName("Generate shifted zone ids - alphabetical order")
    void successful_generate_shifted_zone_id_values() {
        List<String> result = generate(
                CustomType.ZONE_ID,
                3,
                new ConfigDto().setNotNull(true).setRangeType(RangeType.SHIFT.getType()));

        assertThat(result).isEqualTo(ZoneIdGenerator.ZONE_IDS.subList(0, 3));
    }

    @Test
    @DisplayName("Unique zone ids more than available - validation error")
    void fail_generate_unique_zone_id_values() {
        assertThat(generateInvalid(
                        CustomType.ZONE_ID,
                        ZoneIdGenerator.ZONE_IDS.size() + 1,
                        new ConfigDto().setNotNull(true).setUnique(true)))
                .contains("greater than available unique zone ids");
    }

    private List<String> generate(CustomType type, int size, ConfigDto config) {
        return generate(column(type, config), size);
    }

    private List<String> generate(ColumnDto column, int size) {
        byte[] response = sendGenerateRequest(request(column, size));
        return convert(BaseType.STRING, response).stream()
                .map(String.class::cast)
                .toList();
    }

    private String generateInvalid(CustomType type, int size, ConfigDto config) {
        return RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request(column(type, config), size))
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .asString();
    }

    private ColumnDto column(CustomType type, ConfigDto config) {
        return new ColumnDto()
                .setName("column")
                .setType(type.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(config);
    }

    private GenerateTableRequest request(ColumnDto column, int size) {
        return new GenerateTableRequest()
                .setSize(size)
                .setFileType(FileType.CSV.name())
                .setColumns(List.of(column));
    }
}
