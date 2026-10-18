package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.controllers.dto.converters.ConverterDto;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.ConverterType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;

@DisplayName("Mask, pad and truncate converters tests")
public class MaskPadTruncateConvertersTest extends ApiBaseTest {
    private static final String COLUMN = "column";
    private static final String SOURCE = "Hello World 123 Привет Мир";
    private static final int SIZE = 20;

    @Test
    @DisplayName("Apply MASK converter - middle of the string is replaced with mask character")
    void successful_apply_mask() {
        assertThat(generate(converter(ConverterType.MASK, 6, 3))).containsOnly("Hello *****************Мир");
        assertThat(generate(converter(ConverterType.MASK, 0, 4, "x"))).containsOnly("x".repeat(22) + " Мир");
        // Без аргументов маскируется вся строка
        assertThat(generate(converter(ConverterType.MASK))).containsOnly("*".repeat(SOURCE.length()));

        // В строке, которая не длиннее видимой части, маскировать нечего
        assertThat(generate(converter(ConverterType.MASK, 20, 6))).containsOnly(SOURCE);
        assertThat(generate(converter(ConverterType.MASK, Integer.MAX_VALUE, Integer.MAX_VALUE)))
                .containsOnly(SOURCE);
    }

    @Test
    @DisplayName("Apply PAD_LEFT and PAD_RIGHT converters - string is padded to the given length")
    void successful_apply_pad() {
        assertThat(generate(converter(ConverterType.PAD_LEFT, 30, "0"))).containsOnly("0000" + SOURCE);
        assertThat(generate(converter(ConverterType.PAD_RIGHT, 30, "."))).containsOnly(SOURCE + "....");

        // Строка не короче заданной длины не меняется
        assertThat(generate(converter(ConverterType.PAD_LEFT, 5, "0"))).containsOnly(SOURCE);
        assertThat(generate(converter(ConverterType.PAD_RIGHT, SOURCE.length(), "0")))
                .containsOnly(SOURCE);

        // Без символа строка дополняется пробелами. Скобки нужны, чтобы пробелы по краям были видны в CSV
        ConverterDto opening = converter(ConverterType.ADD_BEFORE, "[");
        ConverterDto closing = converter(ConverterType.ADD_AFTER, "]");
        assertThat(generate(List.of(converter(ConverterType.PAD_LEFT, 28), opening, closing), true))
                .containsOnly("[  " + SOURCE + "]");
        assertThat(generate(List.of(converter(ConverterType.PAD_RIGHT, 28), opening, closing), true))
                .containsOnly("[" + SOURCE + "  ]");
    }

    @Test
    @DisplayName("Apply TRUNCATE converter - string is cut to the given length")
    void successful_apply_truncate() {
        assertThat(generate(converter(ConverterType.TRUNCATE, 5))).containsOnly("Hello");
        assertThat(generate(converter(ConverterType.TRUNCATE, 22))).containsOnly("Hello World 123 Привет");

        // Строка не длиннее заданной длины не меняется
        assertThat(generate(converter(ConverterType.TRUNCATE, SOURCE.length()))).containsOnly(SOURCE);
        assertThat(generate(converter(ConverterType.TRUNCATE, 1000))).containsOnly(SOURCE);

        // Пустая строка, в отличие от null, записывается в CSV парой кавычек
        assertThat(generate(converter(ConverterType.TRUNCATE, 0))).containsOnly("\"\"");
    }

    @Test
    @DisplayName("Apply MASK, PAD and TRUNCATE converters to nullable column - null values are kept")
    void successful_apply_converters_to_nullable_column() {
        Map<ConverterDto, String> expected = Map.of(
                converter(ConverterType.MASK, 5, 0), "Hello" + "*".repeat(21),
                converter(ConverterType.PAD_LEFT, 27, "0"), "0" + SOURCE,
                converter(ConverterType.PAD_RIGHT, 27, "0"), SOURCE + "0",
                converter(ConverterType.TRUNCATE, 5), "Hello");

        expected.forEach((converter, value) -> {
            List<String> result = generate(List.of(converter), false);

            // Пустая строка в CSV соответствует null
            assertThat(result).hasSize(SIZE).containsOnly("", value);
        });
    }

    @Test
    @DisplayName("Apply MASK converter with probability - only part of values is masked")
    void successful_apply_mask_with_probability() {
        String masked = "*".repeat(SOURCE.length());

        List<String> result = generate(converter(ConverterType.MASK).setProbability(50));

        assertThat(result).hasSize(SIZE).containsOnly(SOURCE, masked);
    }

    @Test
    @DisplayName("Apply PAD_LEFT as post converter - number is padded with zeros")
    void successful_apply_pad_left_to_number() {
        ColumnDto column = new ColumnDto()
                .setName(COLUMN)
                .setType(BaseType.INTEGER.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConvertToString(true)
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.SHIFT.getType())
                        .setStart("8")
                        .setStep("1"))
                .setPostConverters(List.of(converter(ConverterType.PAD_LEFT, 6, "0")));

        assertThat(generate(column)).startsWith("000008", "000009", "000010", "000011");
    }

    @Test
    @DisplayName("Converters with invalid arguments - validation error")
    void fail_apply_converters_with_invalid_arguments() {
        String notNegative = "must be a non-negative integer";
        sendInvalid(
                converter(ConverterType.MASK, -1),
                "[mask] has invalid arguments: [argument [visibleStart] " + notNegative);
        sendInvalid(converter(ConverterType.MASK, 2, "two"), "argument [visibleEnd] " + notNegative);
        sendInvalid(converter(ConverterType.MASK, 2, 2, "**"), "argument [maskChar] must be a single character");

        sendInvalid(
                converter(ConverterType.PAD_LEFT), "[padLeft] has invalid arguments: [argument [length] is required");
        sendInvalid(
                converter(ConverterType.PAD_RIGHT), "[padRight] has invalid arguments: [argument [length] is required");
        sendInvalid(converter(ConverterType.PAD_LEFT, 1001), "argument [length] must be an integer between 0 and 1000");
        sendInvalid(converter(ConverterType.PAD_RIGHT, 10, "ab"), "argument [padChar] must be a single character");

        sendInvalid(
                converter(ConverterType.TRUNCATE),
                "[truncate] has invalid arguments: [argument [maxLength] is required");
        sendInvalid(converter(ConverterType.TRUNCATE, -1), "argument [maxLength] " + notNegative);
        sendInvalid(converter(ConverterType.TRUNCATE, 1.5), "argument [maxLength] " + notNegative);
    }

    @Test
    @DisplayName("Converters are listed among string converters only")
    void converters_are_listed() {
        RestAssured.given()
                .when()
                .get("/v1/converters")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("converters.find { it.type == 'str' }.names", hasItems("mask", "padLeft", "padRight", "truncate"))
                .body("converters.find { it.type == 'int' }.names", not(hasItem("mask")));
    }

    private List<String> generate(ConverterDto converter) {
        return generate(List.of(converter), true);
    }

    private List<String> generate(List<ConverterDto> converters, boolean notNull) {
        return generate(column(converters, notNull));
    }

    private List<String> generate(ColumnDto column) {
        return convert(BaseType.STRING, sendGenerateRequest(request(column))).stream()
                .map(String.class::cast)
                .toList();
    }

    private void sendInvalid(ConverterDto converter, String expectedMessage) {
        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request(column(List.of(converter), true)))
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("columnErrors." + COLUMN, hasItem(containsString(expectedMessage)));
    }

    private ConverterDto converter(ConverterType type, Object... arguments) {
        return new ConverterDto().setName(type.getName()).setArguments(arguments);
    }

    private GenerateTableRequest request(ColumnDto column) {
        return new GenerateTableRequest()
                .setSize(SIZE)
                .setFileType(FileType.CSV.name())
                .setColumns(List.of(column));
    }

    private ColumnDto column(List<ConverterDto> converters, boolean notNull) {
        return new ColumnDto()
                .setName(COLUMN)
                .setType(BaseType.STRING.getValue())
                .setGenerationStrategy(GenerationStrategy.DICTIONARY.getName())
                .setConfig(new ConfigDto()
                        .setNotNull(notNull)
                        .setRangeType(RangeType.SHIFT.getType())
                        .setDictionaries(Set.of(Set.of(SOURCE))))
                .setConverters(converters);
    }
}
