package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.config.limits.GenerationLimitsCalculationProperties;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.services.limits.GenerationLimits;
import ru.akvine.istochnik.services.limits.GenerationLimitsCalculator;
import ru.akvine.istochnik.services.limits.GenerationLimitsService;

// Стоимость ячейки завышена, чтобы лимиты получились небольшими на машине с любой кучей.
// Пул из общих настроек тестов - один поток и одно место в очереди - принимает две задачи, то есть две колонки
@TestPropertySource(
        properties = {
            "max.generation.calculation.enabled=true",
            "max.generation.calculation.bytes-per-cell=1000000",
            "max.generation.calculation.xlsx-bytes-per-cell=10000000"
        })
@DisplayName("Generation limits are calculated by machine resources")
public class GenerationLimitsCalculationTest extends ApiBaseTest {
    private static final int EXECUTOR_CAPACITY = 2;

    @Autowired
    private GenerationLimitsService generationLimitsService;

    @Autowired
    private GenerationLimitsCalculator generationLimitsCalculator;

    @Autowired
    private GenerationLimitsCalculationProperties calculationProperties;

    private GenerationLimits limits;

    @BeforeEach
    void readLimits() {
        limits = generationLimitsService.getLimits();
    }

    @Test
    @DisplayName("Limits are calculated by heap size and executor instead of settings")
    void limits_are_calculated_by_machine_resources() {
        GenerationLimits expected = generationLimitsCalculator.calculate(
                Runtime.getRuntime().maxMemory(), EXECUTOR_CAPACITY, calculationProperties);

        assertThat(limits).isEqualTo(expected);
        assertThat(limits.maxColumnsSize()).isEqualTo(EXECUTOR_CAPACITY);
        // Значения из настроек не используются
        assertThat(limits.maxRowsSize()).isNotEqualTo(10000);
        assertThat(limits.maxCellsSize()).isNotEqualTo(500000);
    }

    @Test
    @DisplayName("Request of the calculated size - successfully")
    void successful_generate_with_calculated_limits() {
        byte[] file = sendGenerateRequest(request(limits.maxRowsSize(), 1, FileType.CSV));

        assertThat(new String(file, StandardCharsets.UTF_8).lines()).hasSize(limits.maxRowsSize() + 1);
    }

    @Test
    @DisplayName("Request is greater than calculated limits - validation error")
    void fail_generate_over_calculated_limits() {
        sendInvalid(request(1, EXECUTOR_CAPACITY + 1, FileType.CSV))
                .body(containsString("Can't generate columns count = [3] more than max = [2]"));
        sendInvalid(request(limits.maxRowsSize() + 1, 1, FileType.CSV))
                .body(containsString("more than max = [" + limits.maxRowsSize() + "]"));
        sendInvalid(request(limits.maxRowsSize(), 2, FileType.CSV))
                .body(containsString("(rows * columns) more than max = [" + limits.maxCellsSize() + "]"));
    }

    @Test
    @DisplayName("XLSX request is greater than calculated XLSX cells limit - validation error")
    void fail_generate_xlsx_over_calculated_limit() {
        long rows = limits.maxXlsxCellsSize() + 1;

        sendInvalid(request((int) rows, 1, FileType.XLSX))
                .body(containsString("Can't generate cells count = [" + rows + "] (rows * columns) more than max = ["
                        + limits.maxXlsxCellsSize() + "] for file type = XLSX"));
    }

    @Test
    @DisplayName("Schema conversion uses calculated rows limit")
    void schema_conversion_uses_calculated_rows_limit() {
        RestAssured.given()
                .multiPart("file", "V1__init.sql", "CREATE TABLE t (id int);".getBytes(StandardCharsets.UTF_8))
                .queryParam("size", limits.maxRowsSize() + 1)
                .accept(ContentType.JSON)
                .when()
                .post(RestMethods.CONVERT_SCHEMA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(containsString("more than max = [" + limits.maxRowsSize() + "]"));
    }

    private ValidatableResponse sendInvalid(GenerateTableRequest request) {
        return RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private GenerateTableRequest request(int rowsCount, int columnsCount, FileType fileType) {
        List<ColumnDto> columns = IntStream.range(0, columnsCount)
                .mapToObj(index -> new ColumnDto()
                        .setName("column_" + index)
                        .setType(CustomType.UUID.getName())
                        .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                        .setConfig(new ConfigDto()))
                .toList();

        return new GenerateTableRequest()
                .setSize(rowsCount)
                .setFileType(fileType.name())
                .setColumns(columns);
    }
}
