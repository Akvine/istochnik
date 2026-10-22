package ru.akvine.istochnik.api.generators.regex;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.http.HttpStatus;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.controllers.dto.converters.ConverterDto;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.api.ApiBaseTest;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.ConverterType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;

@DisplayName("Regexp tests")
public class GenerateByRegexTests extends ApiBaseTest {
    private static final int SIZE = 30;

    private static final String ORDER_NUMBER = "ORD-[0-9]{6}";
    private static final String CAR_NUMBER = "[ABEKMHOPCTYX][0-9]{3}[ABEKMHOPCTYX]{2}";

    @Test
    @DisplayName("Generate by one regexp - every value matches it")
    void successful_generate_by_one_regexp() {
        List<String> values = generate(config(RangeType.RANDOM, ORDER_NUMBER));

        assertThat(values).hasSize(SIZE).allMatch(value -> value.matches(ORDER_NUMBER));
        assertThat(new HashSet<>(values)).hasSizeGreaterThan(SIZE / 2);
    }

    @Test
    @DisplayName("Generate by several regexps [rangeType = RANDOM] - every value matches one of them")
    void successful_generate_by_random_regexp() {
        List<String> values = generate(config(RangeType.RANDOM, ORDER_NUMBER, CAR_NUMBER));

        assertThat(values).hasSize(SIZE).allMatch(value -> value.matches(ORDER_NUMBER) || value.matches(CAR_NUMBER));
        assertThat(values).anyMatch(value -> value.matches(ORDER_NUMBER));
        assertThat(values).anyMatch(value -> value.matches(CAR_NUMBER));
    }

    @Test
    @DisplayName("Generate by several regexps [rangeType = SHIFT] - regexps are used in turn")
    void successful_generate_by_regexps_in_turn() {
        List<String> values = generate(config(RangeType.SHIFT, ORDER_NUMBER, CAR_NUMBER));

        // С какого выражения начнётся чередование, запрос не определяет: выражения передаются множеством
        String first = values.get(0).matches(ORDER_NUMBER) ? ORDER_NUMBER : CAR_NUMBER;
        String second = first.equals(ORDER_NUMBER) ? CAR_NUMBER : ORDER_NUMBER;

        assertThat(values).hasSize(SIZE);
        for (int index = 0; index < values.size(); index++) {
            assertThat(values.get(index)).matches(index % 2 == 0 ? first : second);
        }
    }

    @Test
    @DisplayName("Generate by regexp [notNull = false] - some values are null")
    void successful_generate_nullable_by_regexp() {
        List<String> values = generate(config(RangeType.RANDOM, ORDER_NUMBER).setNotNull(false));

        // Пустая строка в CSV соответствует null
        assertThat(values).hasSize(SIZE).contains("").allMatch(value -> value.isEmpty() || value.matches(ORDER_NUMBER));
        assertThat(values).anyMatch(value -> !value.isEmpty());
    }

    @Test
    @DisplayName("Generate by regexp with converters - converters are applied to generated values")
    void successful_generate_by_regexp_with_converters() {
        ColumnDto column = column(config(RangeType.RANDOM, ORDER_NUMBER))
                .setConverters(List.of(
                        new ConverterDto().setName(ConverterType.LOWER_CASE.getName()),
                        new ConverterDto()
                                .setName(ConverterType.ADD_AFTER.getName())
                                .setArguments(new Object[] {"!"})));

        assertThat(generate(column)).hasSize(SIZE).allMatch(value -> value.matches("ord-[0-9]{6}!"));
    }

    @Test
    @DisplayName("Generate unique values [rangeType = RANDOM] - every value of regexp is used once")
    void successful_generate_unique_by_regexp() {
        // Выражение задаёт ровно 30 строк, и все они должны найтись, хотя повторы выпадают постоянно
        List<String> values = generate(config(RangeType.RANDOM, "[A-C][0-9]").setUnique(true), 30);

        assertThat(values).hasSize(30).doesNotHaveDuplicates().allMatch(value -> value.matches("[A-C][0-9]"));
    }

    @Test
    @DisplayName("Generate unique values [notNull = false] - null is one more unique value")
    void successful_generate_unique_nullable_by_regexp() {
        List<String> values =
                generate(config(RangeType.RANDOM, "[A-C][0-9]").setUnique(true).setNotNull(false), 31);

        // Пустая строка в CSV соответствует null
        assertThat(values).hasSize(31).doesNotHaveDuplicates().containsOnlyOnce("");
    }

    @Test
    @DisplayName("Generate unique values [rangeType = SHIFT] - regexps are used in turn")
    void successful_generate_unique_by_regexps_in_turn() {
        List<String> values = generate(config(RangeType.SHIFT, "A[0-9]", "B[0-9]").setUnique(true), 20);

        String firstLetter = values.get(0).substring(0, 1);
        assertThat(values).hasSize(20).doesNotHaveDuplicates();
        for (int index = 0; index < values.size(); index++) {
            assertThat(values.get(index).startsWith(firstLetter)).isEqualTo(index % 2 == 0);
        }
    }

    @Test
    @DisplayName("Generate more unique values than regexps set - validation error before generation")
    void fail_generate_more_unique_values_than_regexps_set() {
        sendInvalid(request(column(config(RangeType.RANDOM, "[A-C][0-9]").setUnique(true)), 31))
                .body(
                        "columnErrors.column[0]",
                        startsWith("Rows count = [31] greater than available unique values of regexps = [30]"));
        // Строки разных выражений складываются
        sendInvalid(request(column(config(RangeType.RANDOM, "[AB]", "C[0-9]").setUnique(true)), 13))
                .body("columnErrors.column[0]", containsString("available unique values of regexps = [12]"));
        // С null значений на одно больше
        sendInvalid(request(column(config(RangeType.RANDOM, "[AB]").setUnique(true).setNotNull(false)), 4))
                .body("columnErrors.column[0]", containsString("available unique values of regexps = [3]"));
    }

    @Test
    @DisplayName("Generate unique values in turn by too narrow regexp - validation error before generation")
    void fail_generate_unique_values_in_turn_by_narrow_regexp() {
        // Каждому выражению достаётся по 15 строк, а первое задаёт только 10
        ConfigDto config = config(RangeType.SHIFT, "A[0-9]", "B[0-9]{2}").setUnique(true);

        sendInvalid(request(column(config), 30))
                .body(
                        "columnErrors.column[0]",
                        startsWith("regexp [A[0-9]] sets only [10] unique values, but with rangeType = [shift] "
                                + "it is used for at least [15] rows"));
        // При случайном выборе выражения те же строки находятся
        assertThat(generate(config.setRangeType(RangeType.RANDOM.getType()), 30)).doesNotHaveDuplicates();
    }

    // Выражения пересекаются: по отдельности они задают четыре строки, а вместе - три. Проверка запроса
    // этого не видит, и нехватка обнаруживается при генерации без перебора всех попыток
    @Test
    @Timeout(10)
    @DisplayName("Generate unique values by overlapping regexps - generation error instead of endless search")
    void fail_generate_unique_values_by_overlapping_regexps() {
        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request(column(config(RangeType.RANDOM, "[AB]", "[BC]").setUnique(true)), 4))
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body("code", equalTo("generation.error"))
                .body(
                        "message",
                        containsString("Unique values of [regexp] strategy run out: only [3] unique values were "
                                + "generated for [4] rows"));
    }

    @Test
    @DisplayName("Generate without regexps - validation error")
    void fail_generate_without_regexps() {
        sendInvalid(request(column(new ConfigDto().setRangeType(RangeType.RANDOM.getType()))))
                .body("columnErrors.column", hasItem("field [regexps] can't be empty"));
    }

    // Раньше на таких выражениях генерация зависала на минуты или исчерпывала память
    @Test
    @Timeout(10)
    @DisplayName("Generate by too large or too complex regexps - validation error before generation")
    void fail_generate_by_too_large_regexps() {
        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(SIZE)
                .setFileType(FileType.CSV.name())
                .setColumns(List.of(
                        namedColumn("huge_count", "[a-z]{10000000000000000000}"),
                        namedColumn("long_result", "[a-z]{100000}"),
                        namedColumn("nested", "((a{100}){100}){100}"),
                        namedColumn("complex", "[ab]*a[ab]{20}"),
                        namedColumn("long_regexp", "a".repeat(1001)),
                        namedColumn("syntax", "[a-z")));

        sendInvalid(request)
                .body("columnErrors.huge_count[0]", containsString("is invalid"))
                .body("columnErrors.long_result[0]", containsString("sets strings longer than max = [1000]"))
                .body("columnErrors.nested[0]", containsString("sets strings longer than max = [1000]"))
                .body("columnErrors.complex[0]", containsString("is too complex"))
                .body("columnErrors.long_regexp[0]", containsString("is longer than max = [1000] characters"))
                .body("columnErrors.syntax[0]", containsString("is invalid"));
    }

    @Test
    @DisplayName("Generate by more regexps than allowed in one column - validation error")
    void fail_generate_by_too_many_regexps() {
        Set<String> regexps = new HashSet<>();
        for (int i = 0; i < 11; i++) {
            regexps.add("A" + i + "-[0-9]{3}");
        }

        sendInvalid(request(column(config(RangeType.RANDOM, regexps.toArray(String[]::new)))))
                .body("columnErrors.column", hasItem("regexps count can't be more than max = [10]"));
    }

    private ValidatableResponse sendInvalid(GenerateTableRequest request) {
        return RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("code", equalTo("request.fields.invalid.error"));
    }

    private ColumnDto namedColumn(String name, String regexp) {
        return column(config(RangeType.RANDOM, regexp)).setName(name);
    }

    private List<String> generate(ConfigDto config) {
        return generate(column(config));
    }

    private List<String> generate(ConfigDto config, int size) {
        return generate(request(column(config), size));
    }

    private List<String> generate(ColumnDto column) {
        return generate(request(column));
    }

    private List<String> generate(GenerateTableRequest request) {
        return convert(BaseType.STRING, sendGenerateRequest(request)).stream()
                .map(String::valueOf)
                .toList();
    }

    private GenerateTableRequest request(ColumnDto column) {
        return request(column, SIZE);
    }

    private GenerateTableRequest request(ColumnDto column, int size) {
        return new GenerateTableRequest()
                .setSize(size)
                .setFileType(FileType.CSV.name())
                .setColumns(List.of(column));
    }

    private ColumnDto column(ConfigDto config) {
        return new ColumnDto()
                .setName("column")
                .setType(BaseType.STRING.getValue())
                .setGenerationStrategy(GenerationStrategy.REGEXP.getName())
                .setConfig(config);
    }

    private ConfigDto config(RangeType rangeType, String... regexps) {
        return new ConfigDto()
                .setRangeType(rangeType.getType())
                .setNotNull(true)
                .setRegexps(Set.of(regexps));
    }
}
