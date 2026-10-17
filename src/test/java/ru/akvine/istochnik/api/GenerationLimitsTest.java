package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.services.limits.GenerationLimits;
import ru.akvine.istochnik.services.limits.GenerationLimitsService;

// Лимит строк больше, чем вмещает лист XLSX
@TestPropertySource(
        properties = {
            "max.generation.rows.size=2000000",
            "max.generation.columns.size=30",
            "max.generation.cells.size=3000000"
        })
@DisplayName("Generation limits are taken from settings")
public class GenerationLimitsTest extends ApiBaseTest {

    @Autowired
    private GenerationLimitsService generationLimitsService;

    @Test
    @DisplayName("Calculation is disabled - limits are taken from settings, XLSX has the same cells limit")
    void limits_are_taken_from_settings() {
        assertThat(generationLimitsService.getLimits())
                .isEqualTo(new GenerationLimits(2_000_000, 30, 3_000_000, 3_000_000));
    }

    @Test
    @DisplayName("XLSX request has more rows than the sheet can hold - validation error")
    void fail_generate_xlsx_with_too_many_rows() {
        ColumnDto column = new ColumnDto()
                .setName("id")
                .setType(CustomType.UUID.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto());
        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(1_048_576)
                .setFileType(FileType.XLSX.name())
                .setColumns(List.of(column));

        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(containsString(
                        "Can't generate rows size = [1048576] more than max = [1048575] for file type = XLSX"));
    }
}
