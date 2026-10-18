package ru.akvine.istochnik.api.generators.algorithm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.http.HttpStatus;
import ru.akvine.istochnik.api.ApiBaseTest;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.controllers.dto.converters.ConverterDto;
import ru.akvine.istochnik.enums.*;
import ru.akvine.istochnik.utils.DateTimeUtils;

@DisplayName("Custom types tests")
public class GeneratorCustomTypesTest extends ApiBaseTest {

    @Test
    @DisplayName("Generate constant string values successful")
    void successful_generate_string_values() {
        String constantValue = "CONSTANT";
        int size = 10;
        BaseType type = BaseType.STRING;

        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("string_column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.CONSTANT.getName())
                .setConfig(new ConfigDto().setConstant(constantValue)));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(size)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asByteArray();

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type, response));
        List<?> values = convert(type, response);

        assertThat(values.size()).isEqualTo(size);
    }

    @Test
    @DisplayName("Generate random boolean successful")
    void successful_generate_random_boolean_values() {
        BaseType type = BaseType.BOOLEAN;
        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("bool_column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(
                        new ConfigDto().setRangeType(RangeType.RANDOM.toString().toUpperCase())));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asByteArray();

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type, response));
    }

    @Test
    @DisplayName("Generate shifted boolean successful")
    void successful_generate_shifted_boolean_values() {
        BaseType type = BaseType.BOOLEAN;
        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("bool_column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(
                        new ConfigDto().setRangeType(RangeType.SHIFT.toString().toUpperCase())));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asByteArray();

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type, response));
    }

    @Test
    @DisplayName("Generate uuid successful")
    void successful_uuid_values() {
        CustomType type = CustomType.UUID;

        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("uuid_column")
                .setType(type.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto().setNotNull(true)));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asByteArray();

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type.getBaseType(), response));
        assertThatNoException().isThrownBy(() -> convert(type.getBaseType(), response).stream()
                .map(String.class::cast)
                .forEach(UUID::fromString));
    }

    @Test
    @DisplayName("Generate datetime successful")
    void successful_datetime_values() {
        CustomType type = CustomType.DATETIME;
        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("datetime_column")
                .setType(type.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setNotNull(true)
                        .setRangeType(RangeType.RANDOM.getType().toUpperCase())
                        .setStart("2025-04-02 14:00:00")
                        .setEnd("2025-10-02 14:00:00")));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asByteArray();

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type.getBaseType(), response));

        List<String> result = convert(type.getBaseType(), response).stream()
                .map(String.class::cast)
                .toList();

        assertThat(result.stream().allMatch(DateTimeUtils::isDateTime)).isTrue();
    }

    @Test
    @DisplayName("Generate datetime with format converter successful")
    void successful_datetime_values_with_format_converter() {
        CustomType type = CustomType.DATETIME;
        String pattern = "dd.MM.yyyy HH:mm:ss";
        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("datetime_column")
                .setType(type.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setNotNull(true)
                        .setRangeType(RangeType.RANDOM.getType().toUpperCase())
                        .setStart("2025-01-02 14:00:00")
                        .setEnd("2025-11-02 14:00:00"))
                .setConverters(List.of(new ConverterDto()
                        .setName(ConverterType.DATE_FORMAT.getName())
                        .setArguments(new Object[] {pattern}))));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = sendGenerateRequest(request);

        List<String> result = convert(type.getBaseType(), response).stream()
                .map(String.class::cast)
                .toList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        assertThat(result).hasSize(10);
        assertThatNoException().isThrownBy(() -> result.forEach(value -> LocalDateTime.parse(value, formatter)));
        // Время не должно теряться при форматировании
        assertThat(result.stream().anyMatch(value -> !value.endsWith("00:00:00")))
                .isTrue();
    }

    @Test
    @DisplayName("Generate date successful")
    void successful_date_values() {
        CustomType type = CustomType.DATE;
        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("date_column")
                .setType(type.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.getType().toUpperCase())
                        .setStart("2025-04-02")
                        .setEnd("2025-10-02")));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asByteArray();

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type.getBaseType(), response));
        assertThatNoException().isThrownBy(() -> convert(type.getBaseType(), response).stream()
                .map(String.class::cast)
                .filter(StringUtils::isNotBlank)
                .forEach(DateTimeUtils::toLocalDate));
    }

    @Test
    @DisplayName("Generate time successful")
    void successful_time_values() {
        CustomType type = CustomType.TIME;
        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("time_column")
                .setType(type.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setNotNull(true)
                        .setRangeType(RangeType.RANDOM.getType().toUpperCase())
                        .setStart("14:00:00")
                        .setEnd("19:00:00")));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asByteArray();

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type.getBaseType(), response));
        assertThatNoException().isThrownBy(() -> convert(type.getBaseType(), response).stream()
                .map(String.class::cast)
                .forEach(DateTimeUtils::toLocalTime));
    }

    @Test
    @DisplayName("Generate snils values successful")
    void successful_snils_values() {
        CustomType type = CustomType.SNILS;
        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("snils_column")
                .setType(type.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setNotNull(true)
                        .setRangeType(RangeType.RANDOM.getType().toUpperCase())
                        .setStart("1")
                        .setEnd("20")));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asByteArray();

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type.getBaseType(), response));
    }

    @Test
    @DisplayName("Generate valid snils values successful")
    void successful_valid_snils_values() {
        CustomType type = CustomType.SNILS;
        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("snils_column")
                .setType(type.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.getType().toUpperCase())
                        .setValid(true)
                        .setStart("1")
                        .setEnd("20")));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asByteArray();

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type.getBaseType(), response));
    }

    @Test
    @DisplayName("Generate personal inn values successful")
    void successful_personal_inn_values() {
        CustomType type = CustomType.INN_PERSONAL;
        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("personal_inn_column")
                .setType(type.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setNotNull(true)
                        .setRangeType(RangeType.RANDOM.getType().toUpperCase())
                        .setStart("1")
                        .setEnd("20")));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asByteArray();

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type.getBaseType(), response));
    }

    @Test
    @DisplayName("Generate organization inn values successful")
    void successful_organization_inn_values() {
        CustomType type = CustomType.INN_ORG;
        List<ColumnDto> columnsToGenerate = List.of(new ColumnDto()
                .setName("organization_inn_column")
                .setType(type.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.getType().toUpperCase())
                        .setNotNull(true)
                        .setStart("1")
                        .setEnd("20")));

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asByteArray();

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        assertThatNoException().isThrownBy(() -> convert(type.getBaseType(), response));
    }
}
