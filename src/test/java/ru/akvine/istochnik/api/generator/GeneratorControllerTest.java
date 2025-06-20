package ru.akvine.istochnik.api.generator;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.compozit.commons.utils.DateTimeUtils;
import ru.akvine.istochnik.api.ApiBaseTest;
import ru.akvine.istochnik.api.RestMethods;
import ru.akvine.istochnik.api.providers.DetectorsProvider;
import ru.akvine.istochnik.api.providers.TypeConvertersProvider;
import ru.akvine.istochnik.enums.*;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@DisplayName("GeneratorController tests")
public class GeneratorControllerTest extends ApiBaseTest {

    public GeneratorControllerTest(TypeConvertersProvider typeConvertersProvider,
                                   DetectorsProvider detectorsProvider) {
        super(typeConvertersProvider, detectorsProvider);
    }

    @Test
    @DisplayName("Generate constant string values successful")
    void successful_generate_string_values() {
        String constantValue = "CONSTANT";
        int size = 10;
        BaseType type = BaseType.STRING;

        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("string_column")
                        .setType(type.getValue())
                        .setGenerationStrategy(GenerationStrategy.CONSTANT.getName())
                        .setConfig(new ConfigDto()
                                .setConstant(constantValue)
                        )
        );

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(size)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = RestAssured
                .given()
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
        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("bool_column")
                        .setType(type.getValue())
                        .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                        .setConfig(new ConfigDto()
                                .setRangeType(RangeType.RANDOM.toString().toUpperCase())
                        )
        );

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = RestAssured
                .given()
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
        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("bool_column")
                        .setType(type.getValue())
                        .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                        .setConfig(new ConfigDto()
                                .setRangeType(RangeType.SHIFT.toString().toUpperCase())
                        )
        );

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = RestAssured
                .given()
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

        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("uuid_column")
                        .setType(type.getName())
                        .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                        .setConfig(new ConfigDto()
                                .setNotNull(true))
        );

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = RestAssured
                .given()
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
        assertThatNoException().isThrownBy(() -> convert(type.getBaseType(), response)
                .forEach(value -> {
                    if (value instanceof String) {
                       UUID.fromString((String) value);
                    } else {
                        throw new RuntimeException("Value is not " + String.class.getSimpleName() + " instance!");
                    }
                }));
    }

    @Test
    @DisplayName("Generate datetime successful")
    void successful_datetime_values() {
        CustomType type = CustomType.DATETIME;
        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("datetime_column")
                        .setType(type.getName())
                        .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                        .setConfig(new ConfigDto()
                                .setNotNull(true)
                                .setRangeType(RangeType.RANDOM.getType().toUpperCase())
                                .setStart("2025-04-02 14:00:00")
                                .setEnd("2025-10-02 14:00:00")
                        )
        );

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = RestAssured
                .given()
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
    @DisplayName("Generate date successful")
    void successful_date_values() {
        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("date_column")
                        .setType(CustomType.DATE.getName())
                        .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                        .setConfig(new ConfigDto()
                                .setRangeType(RangeType.RANDOM.getType().toUpperCase())
                                .setStart("2025-04-02")
                                .setEnd("2025-10-02")
                        )
        );

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = RestAssured
                .given()
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

        assertThatNoException().isThrownBy(() -> asString(response));
    }

    @Test
    @DisplayName("Generate time successful")
    void successful_time_values() {
        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("time_column")
                        .setType(CustomType.TIME.getName())
                        .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                        .setConfig(new ConfigDto()
                                .setNotNull(true)
                                .setRangeType(RangeType.RANDOM.getType().toUpperCase())
                                .setStart("14:00:00")
                                .setEnd("19:00:00")
                        )
        );

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = RestAssured
                .given()
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

        assertThatNoException().isThrownBy(() -> asString(response));
    }

    @Test
    @DisplayName("Generate snils values successful")
    void successful_snils_values() {
        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("snils_column")
                        .setType(CustomType.SNILS.getName())
                        .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                        .setConfig(new ConfigDto()
                                .setNotNull(true)
                                .setRangeType(RangeType.RANDOM.getType().toUpperCase())
                                .setStart("1")
                                .setEnd("20")
                        )
        );

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = RestAssured
                .given()
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

        assertThatNoException().isThrownBy(() -> asLong(response));
    }

    @Test
    @DisplayName("Generate valid snils values successful")
    void successful_valid_snils_values() {
        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("snils_column")
                        .setType(CustomType.SNILS.getName())
                        .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                        .setConfig(new ConfigDto()
                                .setRangeType(RangeType.RANDOM.getType().toUpperCase())
                                .setValid(true)
                                .setStart("1")
                                .setEnd("20")
                        )
        );

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = RestAssured
                .given()
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

        assertThatNoException().isThrownBy(() -> asLong(response));
    }

    @Test
    @DisplayName("Generate personal inn values successful")
    void successful_personal_inn_values() {
        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("personal_inn_column")
                        .setType(CustomType.INN_PERSONAL.getName())
                        .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                        .setConfig(new ConfigDto()
                                .setNotNull(true)
                                .setRangeType(RangeType.RANDOM.getType().toUpperCase())
                                .setStart("1")
                                .setEnd("20")
                        )
        );

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = RestAssured
                .given()
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

        assertThatNoException().isThrownBy(() -> asLong(response));
    }

    @Test
    @DisplayName("Generate organization inn values successful")
    void successful_organization_inn_values() {
        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("organization_inn_column")
                        .setType(CustomType.INN_ORG.getName())
                        .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                        .setConfig(new ConfigDto()
                                .setRangeType(RangeType.RANDOM.getType().toUpperCase())
                                .setNotNull(true)
                                .setStart("1")
                                .setEnd("20")
                        )
        );

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(10)
                .setFileType(FileType.CSV.name())
                .setColumns(columnsToGenerate);

        byte[] response = RestAssured
                .given()
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

        assertThatNoException().isThrownBy(() -> asLong(response));
    }
}
