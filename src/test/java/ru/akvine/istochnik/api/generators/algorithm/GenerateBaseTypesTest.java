package ru.akvine.istochnik.api.generators.algorithm;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.api.ApiBaseTest;
import ru.akvine.istochnik.api.configs.RestMethods;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@DisplayName("Base types tests")
public class GenerateBaseTypesTest extends ApiBaseTest {

    @Test
    @DisplayName("Generate random integers successful [not null = true, unique = false]")
    void successful_generate_random_int_values() {
        BaseType type = BaseType.INTEGER;

        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("int_column")
                        .setType(type.getValue())
                        .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                        .setConfig(new ConfigDto()
                                .setRangeType(RangeType.RANDOM.toString())
                                .setStart("0")
                                .setNotNull(true)
                                .setEnd("10")
                        )
        );

        List<Long> expected = List.of(0L, 3L, 3L, 5L, 0L, 7L, 0L, 4L, 9L, 6L);
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

        List<?> result = convert(type, response);
        assertThat(result).isEqualTo(expected);
        assertThat(isRandom(type, result)).isTrue();
    }

    @Test
    @DisplayName("Generate shifted integers successful [not null = false, unique = false]")
    void successful_generate_shifted_int_values() {
        BaseType type = BaseType.INTEGER;

        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("int_column")
                        .setType(type.getValue())
                        .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                        .setConfig(new ConfigDto()
                                .setRangeType(RangeType.SHIFT.toString().toUpperCase())
                                .setStart("0")
                                .setEnd("10")
                                .setStep("1")
                        )
        );

        List<Long> expected = List.of(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);
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

        List<?> result = convert(type, response);

        assertThat(result).isEqualTo(expected);
        assertThat(isShifted(type, result)).isTrue();
    }

    @Test
    @DisplayName("Generate random doubles successful [not null = false, unique = false]")
    void successful_generate_random_double_values() {
        BaseType type = BaseType.DOUBLE;
        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("double_column")
                        .setType(type.getValue())
                        .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                        .setConfig(new ConfigDto()
                                .setRangeType(RangeType.RANDOM.toString().toUpperCase())
                                .setStart("0")
                                .setEnd("50")
                        )
        );

        List<Double> expected = Arrays.asList(
                null, null, 30.317, 27.522, null, null, 12.639, null, null, null
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

        List<?> result = convert(type, response);

        assertThat(result).isEqualTo(expected);
        assertThat(isRandom(type, result)).isTrue();
    }

    @Test
    @DisplayName("Generate shifted doubles successful [not null = false, unique = false]")
    void successful_generate_shifted_double_values() {
        BaseType type = BaseType.DOUBLE;
        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("double_column")
                        .setType(type.getValue())
                        .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                        .setConfig(new ConfigDto()
                                .setRangeType(RangeType.SHIFT.toString().toUpperCase())
                                .setStart("0")
                                .setEnd("10")
                                .setStep("1")
                        )
        );

        List<Double> expected = List.of(
                0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0
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

        List<?> result = convert(type, response);

        assertThat(result).isEqualTo(expected);
        assertThat(isShifted(type, result)).isTrue();
    }
}
