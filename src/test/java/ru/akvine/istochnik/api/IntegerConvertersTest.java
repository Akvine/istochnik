package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.ConverterDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.enums.*;

@DisplayName("Integer converters tests")
public class IntegerConvertersTest extends ApiBaseTest {
    @Test
    @DisplayName("Apply ABS converter to INTEGER - successfully")
    void successful_apply_abs_at_random_int_values() {
        BaseType type = BaseType.INTEGER;

        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("int_column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.toString())
                        .setStart("-10")
                        .setNotNull(true)
                        .setEnd("0"))
                .setConverters(List.of(new ConverterDto().setName(ConverterType.ABS.getName()))));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = sendGenerateRequest(request);

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type, response));

        List<?> result = convert(type, response);
        List<Long> casted = result.stream().map(Long.class::cast).toList();
        assertThat(casted.stream().allMatch(value -> value >= 0)).isTrue();
        assertThat(isRandom(type, result)).isTrue();
    }

    @Test
    @DisplayName("Apply DIVIDE converter to INTEGER - successfully")
    void successful_apply_divide_at_int_values() {
        BaseType type = BaseType.INTEGER;

        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("int_column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.toString())
                        .setStart("0")
                        .setNotNull(true)
                        .setEnd("10")
                        .setSeed(SEED))
                .setConverters(List.of(new ConverterDto()
                        .setName(ConverterType.DIVIDE.getName())
                        .setArguments(new Object[] {"2.0D"}))));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        List<Long> expected = List.of(1L, 2L, 2L, 1L, 4L, 3L, 2L, 0L, 3L, 0L);
        byte[] response = sendGenerateRequest(request);

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type, response));

        List<?> result = convert(type, response);
        assertThat(result).isEqualTo(expected);
        assertThat(isRandom(type, result)).isTrue();
    }

    @Test
    @DisplayName("Apply DIVIDE converter to INTEGER with zero argument - error")
    void divide_by_zero_at_int_values() {
        BaseType type = BaseType.INTEGER;

        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("int_column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.toString())
                        .setStart("0")
                        .setNotNull(true)
                        .setEnd("10"))
                .setConverters(List.of(new ConverterDto()
                        .setName(ConverterType.DIVIDE.getName())
                        .setArguments(new Object[] {"0.0D"}))));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        String expectedMessage = "converter with name [divide] has invalid arguments: [Division by zero]";
        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("columnErrors", allOf(hasEntry(equalTo("int_column"), hasItems(expectedMessage))));
    }

    @Test
    @DisplayName("Apply DIVIDE converter to INTEGER with has no arguments - error")
    void divide_converter_has_no_args_error() {
        BaseType type = BaseType.INTEGER;

        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("int_column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.toString())
                        .setStart("0")
                        .setNotNull(true)
                        .setEnd("10"))
                .setConverters(List.of(new ConverterDto().setName(ConverterType.DIVIDE.getName()))));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        String expectedMessage =
                "converter with name [divide] has invalid arguments: [arguments can't be null or empty!]";
        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("columnErrors", allOf(hasEntry(equalTo("int_column"), hasItems(expectedMessage))));
    }

    @Test
    @DisplayName("Apply PLUS converter to INTEGER - successfully")
    void successful_apply_plus_converter_at_int_values() {
        BaseType type = BaseType.INTEGER;

        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("int_column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.toString())
                        .setStart("0")
                        .setNotNull(true)
                        .setEnd("10")
                        .setSeed(SEED))
                .setConverters(List.of(
                        new ConverterDto().setName(ConverterType.PLUS.getName()).setArguments(new Object[] {"1.0D"}))));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        List<Long> expected = List.of(4L, 6L, 5L, 3L, 10L, 7L, 6L, 2L, 7L, 2L);
        byte[] response = sendGenerateRequest(request);

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type, response));

        List<?> result = convert(type, response);
        assertThat(result).isEqualTo(expected);
        assertThat(isRandom(type, result)).isTrue();
    }

    @Test
    @DisplayName("Apply PLUS converter to INTEGER with has no args - error")
    void plus_converter_has_no_args_error() {
        BaseType type = BaseType.INTEGER;

        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("int_column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.toString())
                        .setStart("0")
                        .setNotNull(true)
                        .setEnd("10"))
                .setConverters(List.of(new ConverterDto().setName(ConverterType.PLUS.getName()))));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        String expectedMessage =
                "converter with name [plus] has invalid arguments: [arguments can't be null or empty!]";
        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("columnErrors", allOf(hasEntry(equalTo("int_column"), hasItems(expectedMessage))));
    }

    @Test
    @DisplayName("Apply MINUS converter to INTEGER - successfully")
    void successful_apply_minus_converter_at_int_values() {
        BaseType type = BaseType.INTEGER;

        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("int_column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.toString())
                        .setStart("0")
                        .setNotNull(true)
                        .setEnd("10")
                        .setSeed(SEED))
                .setConverters(List.of(new ConverterDto()
                        .setName(ConverterType.MINUS.getName())
                        .setArguments(new Object[] {"1.0D"}))));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        List<Long> expected = List.of(2L, 4L, 3L, 1L, 8L, 5L, 4L, 0L, 5L, 0L);
        byte[] response = sendGenerateRequest(request);

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type, response));

        List<?> result = convert(type, response);
        assertThat(result).isEqualTo(expected);
        assertThat(isRandom(type, result)).isTrue();
    }

    @Test
    @DisplayName("Apply MINUS converter to INTEGER with has no args - error")
    void minus_converter_has_no_args_error() {
        BaseType type = BaseType.INTEGER;

        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("int_column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.toString())
                        .setStart("0")
                        .setNotNull(true)
                        .setEnd("10"))
                .setConverters(List.of(new ConverterDto().setName(ConverterType.MINUS.getName()))));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        String expectedMessage =
                "converter with name [minus] has invalid arguments: [arguments can't be null or empty!]";
        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("columnErrors", allOf(hasEntry(equalTo("int_column"), hasItems(expectedMessage))));
    }

    @Test
    @DisplayName("Apply MOD converter to INTEGER - successfully")
    void successful_apply_mod_converter_at_int_values() {
        BaseType type = BaseType.INTEGER;

        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("int_column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.toString())
                        .setStart("0")
                        .setNotNull(true)
                        .setEnd("10"))
                .setConverters(List.of(
                        new ConverterDto().setName(ConverterType.MOD.getName()).setArguments(new Object[] {"1.0D"}))));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        List<Long> expected = List.of(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
        byte[] response = sendGenerateRequest(request);

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type, response));

        List<?> result = convert(type, response);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Apply MOD converter to INTEGER with has no args - error")
    void mod_converter_has_no_args_error() {
        BaseType type = BaseType.INTEGER;

        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("int_column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.toString())
                        .setStart("0")
                        .setNotNull(true)
                        .setEnd("10"))
                .setConverters(List.of(new ConverterDto().setName(ConverterType.MOD.getName()))));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        String expectedMessage = "converter with name [mod] has invalid arguments: [arguments can't be null or empty!]";
        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("columnErrors", allOf(hasEntry(equalTo("int_column"), hasItems(expectedMessage))));
    }

    @Test
    @DisplayName("Apply FACTORIAL converter to INTEGER - successfully")
    void successful_apply_factorial_converter_at_int_values() {
        BaseType type = BaseType.INTEGER;

        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("int_column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.toString())
                        .setStart("0")
                        .setNotNull(true)
                        .setEnd("10")
                        .setSeed(SEED))
                .setConverters(List.of(new ConverterDto()
                        .setName(ConverterType.FACTORIAL.getName())
                        .setArguments(new Object[] {"1.0D"}))));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        List<Long> expected = List.of(6L, 120L, 24L, 2L, 362880L, 720L, 120L, 1L, 720L, 1L);
        byte[] response = sendGenerateRequest(request);

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type, response));

        List<?> result = convert(type, response);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Apply NEGATIVE converter to INTEGER [notNull = true] - successfully")
    void successful_apply_negative_converter_at_int_values_with_notNull() {
        BaseType type = BaseType.INTEGER;

        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("int_column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.toString())
                        .setStart("0")
                        .setEnd("10")
                        .setSeed(SEED))
                .setConverters(List.of(new ConverterDto().setName(ConverterType.NEGATIVE.getName()))));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        // TODO: можно заменить на Arrays.asList(...);
        List<Long> expected = new ArrayList<>();
        expected.add(null);
        expected.add(-4L);
        expected.add(0L);
        expected.add(0L);
        expected.add(-6L);
        expected.add(-5L);
        expected.add(null);
        expected.add(-5L);
        expected.add(null);
        expected.add(null);
        byte[] response = sendGenerateRequest(request);

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type, response));

        List<?> result = convert(type, response);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Apply NEGATIVE converter to INTEGER [notNull = false] - successfully")
    void successful_apply_negative_converter_at_int_values() {
        BaseType type = BaseType.INTEGER;

        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("int_column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.toString())
                        .setStart("0")
                        .setNotNull(true)
                        .setEnd("10")
                        .setSeed(SEED))
                .setConverters(List.of(new ConverterDto().setName(ConverterType.NEGATIVE.getName()))));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        List<Long> expected = List.of(-3L, -5L, -4L, -2L, -9L, -6L, -5L, -1L, -6L, -1L);
        byte[] response = sendGenerateRequest(request);

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type, response));

        List<?> result = convert(type, response);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Apply POW converter to INTEGER [notNull = false] - successfully")
    void successful_apply_pow_converter_at_int_values() {
        BaseType type = BaseType.INTEGER;

        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("int_column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.toString())
                        .setStart("0")
                        .setNotNull(true)
                        .setEnd("10")
                        .setSeed(SEED))
                .setConverters(List.of(
                        new ConverterDto().setName(ConverterType.POW.getName()).setArguments(new Object[] {"2.0D"}))));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        List<Long> expected = List.of(9L, 25L, 16L, 4L, 81L, 36L, 25L, 1L, 36L, 1L);
        byte[] response = sendGenerateRequest(request);

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type, response));

        List<?> result = convert(type, response);
        assertThat(result).isEqualTo(expected);
        assertThat(isRandom(type, result)).isTrue();
    }

    @Test
    @DisplayName("Apply POW converter to INTEGER [notNull = true] - successfully")
    void successful_apply_pow_converter_at_int_values_with_null() {
        BaseType type = BaseType.INTEGER;

        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("int_column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.toString())
                        .setStart("0")
                        .setEnd("10")
                        .setSeed(SEED))
                .setConverters(List.of(
                        new ConverterDto().setName(ConverterType.POW.getName()).setArguments(new Object[] {"2.0D"}))));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        List<Long> expected = Arrays.asList(null, 16L, 0L, 0L, 36L, 25L, null, 25L, null, null);
        byte[] response = sendGenerateRequest(request);

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type, response));

        List<?> result = convert(type, response);
        assertThat(result).isEqualTo(expected);
        assertThat(isRandom(type, result)).isTrue();
    }

    @Test
    @DisplayName("Apply POW converter to INTEGER with has no args - error")
    void pow_converter_has_no_args_error() {
        BaseType type = BaseType.INTEGER;

        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("int_column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.toString())
                        .setStart("0")
                        .setNotNull(true)
                        .setEnd("10"))
                .setConverters(List.of(new ConverterDto().setName(ConverterType.POW.getName()))));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        String expectedMessage = "converter with name [pow] has invalid arguments: [arguments can't be null or empty!]";
        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("columnErrors", allOf(hasEntry(equalTo("int_column"), hasItems(expectedMessage))));
    }

    @Test
    @DisplayName("Apply SHUFFLE converter to INTEGER - successfully")
    void successful_apply_shuffle_converter_at_int_values() {
        BaseType type = BaseType.INTEGER;

        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("int_column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.toString())
                        .setStart("0")
                        .setNotNull(true)
                        .setEnd("10")
                        .setSeed(SEED))
                .setConverters(List.of(new ConverterDto().setName(ConverterType.SHUFFLE.getName()))));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        List<Long> expected = List.of(6L, 5L, 6L, 1L, 5L, 1L, 4L, 3L, 2L, 9L);
        byte[] response = sendGenerateRequest(request);

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type, response));

        List<?> result = convert(type, response);
        assertThat(result).isEqualTo(expected);
        assertThat(isRandom(type, result)).isTrue();
    }
}
