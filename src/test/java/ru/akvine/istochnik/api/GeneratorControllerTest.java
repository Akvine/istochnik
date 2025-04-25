package ru.akvine.istochnik.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@DisplayName("GeneratorController tests")
public class GeneratorControllerTest extends ApiBaseTest {

    @Test
    @DisplayName("Generate random integers successful")
    void successful_generate_random_int_values() {
        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("int_column")
                        .setType(BaseType.INTEGER.getValue())
                        .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                        .setConfig(new ConfigDto()
                                .setRangeType(RangeType.RANDOM.toString().toUpperCase())
                                .setStart("0")
                                .setEnd("20")
                        )
        );

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(1000)
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

        List<Long> values = asLong(response);
        assertThat(isRandom(values)).isTrue();
    }

    @Test
    @DisplayName("Generate shifted integers successful")
    void successful_generate_shifted_int_values() {
        List<ColumnDto> columnsToGenerate = List.of(
                new ColumnDto()
                        .setName("int_column")
                        .setType(BaseType.INTEGER.getValue())
                        .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                        .setConfig(new ConfigDto()
                                .setRangeType(RangeType.SHIFT.toString().toUpperCase())
                                .setStart("0")
                                .setEnd("10")
                                .setStep("1")
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

        List<Long> values = asLong(response);
        assertThat(isShifted(values)).isTrue();
    }
}
