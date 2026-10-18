package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.controllers.dto.converters.ConverterDto;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.ConverterType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;

// Лимиты словарей уменьшены, чтобы для их превышения не приходилось отправлять большие запросы
@TestPropertySource(properties = {"max.dictionaries.per.column=2", "max.dictionary.elements.count=3"})
@DisplayName("Generation request validation tests")
public class GenerationValidationTest extends ApiBaseTest {
    private static final String COLUMN = "column";
    private static final int SIZE = 10;
    private static final int MAX_ROWS = 10_000;

    private static final String RANDOM = RangeType.RANDOM.getType();
    private static final String SHIFT = RangeType.SHIFT.getType();

    private static final String RANGE_TYPE_IS_BLANK = "field [rangeType] can't be blank";
    private static final String RANGE_TYPE_IS_UNKNOWN = "Range type = [spiral] is not supported by app!";
    private static final String NOT_ENOUGH_ROWS =
            "field [end] or [step] is invalid. Possible generated rows less than size";
    private static final String END_NOT_GREATER_START = "field [end] must be greater than field [start]";
    private static final String END_LESS_START = "field [end] can't be less than field [start]";

    @Test
    @DisplayName("Invalid size, file type and table name - general errors")
    void fail_generate_with_invalid_request_fields() {
        ColumnDto column =
                intColumn(new ConfigDto().setRangeType(RANDOM).setStart("1").setEnd("10"));

        expectGeneralError(request(-1, FileType.CSV.name(), column), "size can't be less than 0");
        expectGeneralError(
                request(MAX_ROWS + 1, FileType.CSV.name(), column),
                "Can't generate rows size = [" + (MAX_ROWS + 1) + "] more than max = [" + MAX_ROWS + "]");
        expectGeneralError(request(SIZE, "pdf", column), "Target file type = [pdf] is not supported by app");
        expectGeneralError(
                request(SIZE, FileType.SQL.name(), column), "Field 'tableName' can't be blank for file type = SQL");

        // Все общие ошибки возвращаются сразу, а не по одной
        send(request(-1, "pdf", column))
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("code", equalTo("request.fields.invalid.error"))
                .body(
                        "generalInfoErrors",
                        allOf(
                                containsString("size can't be less than 0"),
                                containsString("Target file type = [pdf] is not supported by app")));
    }

    @Test
    @DisplayName("Unknown strategy or type - column error")
    void fail_generate_with_unknown_strategy_or_type() {
        ColumnDto unknownStrategy = new ColumnDto()
                .setName(COLUMN)
                .setType(BaseType.INTEGER.getValue())
                .setGenerationStrategy("magic")
                .setConfig(new ConfigDto());
        expectColumnError(unknownStrategy, "Generation strategy = [magic] is not supported by app!");

        expectColumnError(column("hologram", new ConfigDto()), "field [type] has invalid value");
    }

    @Test
    @DisplayName("Invalid range of INTEGER column - column errors")
    void fail_generate_integer_with_invalid_range() {
        expectColumnError(
                intColumn(new ConfigDto().setRangeType(RANDOM).setStart("abc").setEnd("10")),
                "field [start] is not an integer");
        expectColumnError(
                intColumn(new ConfigDto().setRangeType(RANDOM).setStart("1").setEnd("abc")),
                "field [end] is not an integer");
        expectColumnError(
                intColumn(new ConfigDto().setRangeType(RANDOM).setStart("10").setEnd("1")), END_LESS_START);
        expectColumnError(intColumn(new ConfigDto().setStart("1").setEnd("10")), RANGE_TYPE_IS_BLANK);
        expectColumnError(
                intColumn(new ConfigDto().setRangeType("spiral").setStart("1").setEnd("10")), RANGE_TYPE_IS_UNKNOWN);

        expectColumnError(
                intColumn(new ConfigDto().setRangeType(SHIFT).setStart("1").setStep("abc")),
                "field [step] is not an integer");
        expectColumnError(
                intColumn(new ConfigDto()
                        .setRangeType(SHIFT)
                        .setStart("1")
                        .setStep("1")
                        .setEnd("abc")),
                "field [end] is not an integer");
        // От 1 до 5 с шагом 1 получается меньше значений, чем строк в запросе
        expectColumnError(
                intColumn(new ConfigDto()
                        .setRangeType(SHIFT)
                        .setStart("1")
                        .setStep("1")
                        .setEnd("5")),
                NOT_ENOUGH_ROWS);
    }

    @Test
    @DisplayName("Invalid range of DOUBLE column - column errors")
    void fail_generate_double_with_invalid_range() {
        expectColumnError(
                doubleColumn(
                        new ConfigDto().setRangeType(RANDOM).setStart("abc").setEnd("10")),
                "field [start] is not a double");
        expectColumnError(
                doubleColumn(new ConfigDto().setRangeType(RANDOM).setStart("1").setEnd("abc")),
                "field [end] is not a double");
        expectColumnError(
                doubleColumn(new ConfigDto().setRangeType(RANDOM).setStart("10").setEnd("1")), END_LESS_START);
        expectColumnError(doubleColumn(new ConfigDto().setStart("1").setEnd("10")), RANGE_TYPE_IS_BLANK);

        expectColumnError(
                doubleColumn(new ConfigDto()
                        .setRangeType(SHIFT)
                        .setStart("1")
                        .setEnd("100")
                        .setStep("abc")),
                "field [step] is not a double");
        expectColumnError(
                doubleColumn(new ConfigDto()
                        .setRangeType(SHIFT)
                        .setStart("1")
                        .setEnd("100")
                        .setStep("-1")),
                "field [step] can't be less than 0");
        expectColumnError(
                doubleColumn(new ConfigDto()
                        .setRangeType(SHIFT)
                        .setStart("1")
                        .setEnd("2")
                        .setStep("0.5")),
                NOT_ENOUGH_ROWS);
    }

    @Test
    @DisplayName("Invalid length of STRING column - column error, length in field end is accepted")
    void fail_generate_string_with_invalid_length() {
        String lengthError = "field [length] must be set and be greater than 0";
        expectColumnError(stringColumn(new ConfigDto()), lengthError);
        expectColumnError(stringColumn(new ConfigDto().setLength(0)), lengthError);
        expectColumnError(stringColumn(new ConfigDto().setEnd("abc")), lengthError);

        // Для обратной совместимости длину можно передать в поле end
        byte[] response = sendGenerateRequest(request(
                SIZE,
                FileType.CSV.name(),
                stringColumn(new ConfigDto().setEnd("7").setNotNull(true))));
        assertThat(convert(BaseType.STRING, response))
                .hasSize(SIZE)
                .allMatch(value -> String.valueOf(value).length() == 7);
    }

    @Test
    @DisplayName("Unknown converter, invalid probability or arguments - column errors")
    void fail_generate_with_invalid_converters() {
        ConfigDto intRange = new ConfigDto().setRangeType(RANDOM).setStart("1").setEnd("10");
        ConfigDto doubleRange =
                new ConfigDto().setRangeType(RANDOM).setStart("1").setEnd("10");
        ConfigDto length = new ConfigDto().setLength(5);

        String unknown = "converter with name [fly] is not supported by app";
        ConverterDto fly = new ConverterDto().setName("fly");
        expectColumnError(intColumn(intRange).setConverters(List.of(fly)), unknown);
        expectColumnError(doubleColumn(doubleRange).setConverters(List.of(fly)), unknown);
        expectColumnError(stringColumn(length).setConverters(List.of(fly)), unknown);

        expectColumnError(
                intColumn(intRange)
                        .setConverters(List.of(converter(ConverterType.ABS).setProbability(0))),
                "for converter with name [abs] invalid probability = [0]. Can be only between 0 and 100");
        expectColumnError(
                doubleColumn(doubleRange)
                        .setConverters(List.of(converter(ConverterType.ABS).setProbability(101))),
                "for converter with name [abs] invalid probability = [101]. Can be only between 0 and 100");
        expectColumnError(
                stringColumn(length)
                        .setConverters(List.of(converter(ConverterType.TRIM).setProbability(-1))),
                "for converter with name [trim] invalid probability = [-1]. Can be only between 0 and 100");

        expectColumnError(
                doubleColumn(doubleRange).setConverters(List.of(converter(ConverterType.ROUND))),
                "converter with name [round] has invalid arguments: [arguments can't be null or empty!]");
        expectColumnError(
                stringColumn(length).setConverters(List.of(converter(ConverterType.SUBSTRING, "1"))),
                "converter with name [substring] has invalid arguments: [arguments count can't be less than 2]");
    }

    @Test
    @DisplayName("Invalid range of DECIMAL column - column errors")
    void fail_generate_decimal_with_invalid_range() {
        String decimal = CustomType.DECIMAL.getName();
        String startError = "field [start] is not a decimal with no more than 38 integer and 38 fraction digits";
        String endError = "field [end] is not a decimal with no more than 38 integer and 38 fraction digits";

        expectColumnError(column(decimal, new ConfigDto().setStart("0.0").setEnd("1.0")), RANGE_TYPE_IS_BLANK);
        expectColumnError(
                column(
                        decimal,
                        new ConfigDto().setRangeType("spiral").setStart("0.0").setEnd("1.0")),
                RANGE_TYPE_IS_UNKNOWN);

        expectColumnError(
                column(
                        decimal,
                        new ConfigDto().setRangeType(RANDOM).setStart("abc").setEnd("1.0")),
                startError);
        // Слишком длинная запись числа отклоняется, не доходя до разбора
        expectColumnError(
                column(
                        decimal,
                        new ConfigDto()
                                .setRangeType(RANDOM)
                                .setStart("1".repeat(100))
                                .setEnd("1.0")),
                startError);
        expectColumnError(
                column(
                        decimal,
                        new ConfigDto().setRangeType(RANDOM).setStart("0.0").setEnd("abc")),
                endError);
        expectColumnError(
                column(
                        decimal,
                        new ConfigDto().setRangeType(RANDOM).setStart("5.0").setEnd("5.0")),
                END_NOT_GREATER_START);
        // От 0.0 до 0.3 - три значения, четвёртым уникальным значением может быть null
        expectColumnError(
                column(
                        decimal,
                        new ConfigDto()
                                .setRangeType(RANDOM)
                                .setStart("0.0")
                                .setEnd("0.3")
                                .setUnique(true)),
                "Rows count = [" + SIZE + "] greater than available unique values = [4]");

        expectColumnError(
                column(
                        decimal,
                        new ConfigDto().setRangeType(SHIFT).setStart("0.0").setStep("abc")),
                "field [step] is not a decimal with no more than 38 integer and 38 fraction digits");
        expectColumnError(
                column(
                        decimal,
                        new ConfigDto().setRangeType(SHIFT).setStart("0.0").setStep("0")),
                "field [step] must be greater than 0");
        expectColumnError(
                column(
                        decimal,
                        new ConfigDto()
                                .setRangeType(SHIFT)
                                .setStart("0.0")
                                .setStep("0.1")
                                .setEnd("abc")),
                endError);
        expectColumnError(
                column(
                        decimal,
                        new ConfigDto()
                                .setRangeType(SHIFT)
                                .setStart("abc")
                                .setStep("0.1")
                                .setEnd("1.0")),
                startError);
        expectColumnError(
                column(
                        decimal,
                        new ConfigDto()
                                .setRangeType(SHIFT)
                                .setStart("5.0")
                                .setStep("0.1")
                                .setEnd("1.0")),
                END_NOT_GREATER_START);
    }

    @Test
    @DisplayName("Invalid range of ZONED_DATETIME column - column errors")
    void fail_generate_zoned_datetime_with_invalid_range() {
        String zoned = CustomType.ZONED_DATETIME.getName();
        String start = "2025-01-01 00:00:00 Europe/Moscow";
        String later = "2025-01-01 00:00:05 Europe/Moscow";
        String earlier = "2024-12-31 00:00:00 Europe/Moscow";
        String startError = "field [start] is not a zoned datetime";
        String endError = "field [end] is not a zoned datetime";

        expectColumnError(
                column(
                        zoned,
                        new ConfigDto().setRangeType("spiral").setStart(start).setEnd(later)),
                RANGE_TYPE_IS_UNKNOWN);

        expectColumnError(
                column(
                        zoned,
                        new ConfigDto().setRangeType(RANDOM).setStart("abc").setEnd(later)),
                startError);
        expectColumnError(
                column(
                        zoned,
                        new ConfigDto().setRangeType(RANDOM).setStart(start).setEnd("abc")),
                endError);
        expectColumnError(
                column(
                        zoned,
                        new ConfigDto().setRangeType(RANDOM).setStart(start).setEnd(earlier)),
                END_NOT_GREATER_START);
        // За пять секунд - пять значений, шестым уникальным значением может быть null
        expectColumnError(
                column(
                        zoned,
                        new ConfigDto()
                                .setRangeType(RANDOM)
                                .setStart(start)
                                .setEnd(later)
                                .setUnique(true)),
                "Rows count = [" + SIZE + "] greater than available unique values = [6]");

        expectColumnError(
                column(
                        zoned,
                        new ConfigDto().setRangeType(SHIFT).setStart(start).setStep("abc")),
                "field [step] is not an integer count of seconds");
        expectColumnError(
                column(
                        zoned,
                        new ConfigDto().setRangeType(SHIFT).setStart(start).setStep("0")),
                "field [step] must be greater than 0");
        expectColumnError(
                column(
                        zoned,
                        new ConfigDto().setRangeType(SHIFT).setStart(start).setEnd("abc")),
                endError);
        expectColumnError(
                column(
                        zoned,
                        new ConfigDto().setRangeType(SHIFT).setStart("abc").setEnd(later)),
                startError);
        expectColumnError(
                column(
                        zoned,
                        new ConfigDto().setRangeType(SHIFT).setStart(start).setEnd(earlier)),
                END_LESS_START);
        // За пять секунд с шагом в секунду получается меньше значений, чем строк в запросе
        expectColumnError(
                column(
                        zoned,
                        new ConfigDto()
                                .setRangeType(SHIFT)
                                .setStart(start)
                                .setEnd(later)
                                .setStep("1")),
                NOT_ENOUGH_ROWS);
    }

    @Test
    @DisplayName("Invalid dictionaries - column errors")
    void fail_generate_with_invalid_dictionaries() {
        expectColumnError(dictionaryColumn(new ConfigDto()), "field [dictionaries] can't be empty");
        expectColumnError(
                dictionaryColumn(new ConfigDto().setDictionaries(Set.of(Set.of()))),
                "field [dictionaries] can't be empty");

        expectColumnError(
                dictionaryColumn(new ConfigDto().setDictionaries(Set.of(Set.of("a"), Set.of("b"), Set.of("c")))),
                "dictionaries count can't be more than max = [2]");
        expectColumnError(
                dictionaryColumn(new ConfigDto().setDictionaries(Set.of(Set.of("a", "b", "c", "d")))),
                "Dictionary has more max limit = [4] elements count = [3]");

        ConfigDto unique = new ConfigDto()
                .setRangeType(RANDOM)
                .setUnique(true)
                .setNotNull(true)
                .setDictionaries(Set.of(Set.of("a", "b"), Set.of("b", "c")));
        // Одинаковые элементы разных словарей считаются одним значением
        expectColumnError(
                dictionaryColumn(unique), "Rows count = [" + SIZE + "] greater than available unique elements = [3]");
        // Если колонка может содержать null, он выступает ещё одним уникальным значением
        expectColumnError(
                dictionaryColumn(unique.setNotNull(false)),
                "Rows count = [" + SIZE + "] greater than available unique elements = [4]");
    }

    @Test
    @DisplayName("Errors of several columns are returned together")
    void fail_generate_with_errors_in_several_columns() {
        ColumnDto first = intColumn(
                        new ConfigDto().setRangeType(RANDOM).setStart("abc").setEnd("10"))
                .setName("first");
        ColumnDto second = stringColumn(new ConfigDto()).setName("second");
        ColumnDto valid = stringColumn(new ConfigDto().setLength(5)).setName("valid");

        send(new GenerateTableRequest()
                        .setSize(SIZE)
                        .setFileType(FileType.CSV.name())
                        .setColumns(List.of(first, second, valid)))
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("status", equalTo("FAIL"))
                .body("columnErrors.keySet()", hasItem("first"))
                .body("columnErrors.first", hasItem("field [start] is not an integer"))
                .body("columnErrors.second", hasItem("field [length] must be set and be greater than 0"))
                .body("columnErrors.size()", equalTo(2));
    }

    private void expectColumnError(ColumnDto column, String expectedMessage) {
        send(request(SIZE, FileType.CSV.name(), column))
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("code", equalTo("request.fields.invalid.error"))
                .body("columnErrors." + COLUMN, hasItem(containsString(expectedMessage)));
    }

    private void expectGeneralError(GenerateTableRequest request, String expectedMessage) {
        send(request)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("code", equalTo("request.fields.invalid.error"))
                .body("generalInfoErrors", containsString(expectedMessage));
    }

    private ValidatableResponse send(GenerateTableRequest request) {
        return RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then();
    }

    private GenerateTableRequest request(int size, String fileType, ColumnDto column) {
        return new GenerateTableRequest().setSize(size).setFileType(fileType).setColumns(List.of(column));
    }

    private ColumnDto intColumn(ConfigDto config) {
        return column(BaseType.INTEGER.getValue(), config);
    }

    private ColumnDto doubleColumn(ConfigDto config) {
        return column(BaseType.DOUBLE.getValue(), config);
    }

    private ColumnDto stringColumn(ConfigDto config) {
        return column(BaseType.STRING.getValue(), config);
    }

    private ColumnDto dictionaryColumn(ConfigDto config) {
        return column(BaseType.STRING.getValue(), config)
                .setGenerationStrategy(GenerationStrategy.DICTIONARY.getName());
    }

    private ColumnDto column(String type, ConfigDto config) {
        return new ColumnDto()
                .setName(COLUMN)
                .setType(type)
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(config);
    }

    private ConverterDto converter(ConverterType type, Object... arguments) {
        return new ConverterDto().setName(type.getName()).setArguments(arguments);
    }
}
