package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;

// Очередь пула в общих настройках тестов вмещает одну задачу, поэтому для запроса с несколькими колонками она увеличена
@TestPropertySource(
        properties = {
            "max.generation.rows.size=20",
            "max.generation.columns.size=5",
            "max.generation.cells.size=12",
            "parallel.executor.threads-count=2",
            "parallel.executor.queue-capacity=10"
        })
@DisplayName("Cells count limit tests")
public class GenerationCellsLimitTest extends ApiBaseTest {
    private static final String CELLS_ERROR = "Can't generate cells count";

    @Test
    @DisplayName("Cells count equals to max - successfully")
    void successful_generate_with_max_cells_count() {
        byte[] response = sendGenerateRequest(request(4, 3));

        List<String> lines = new String(response).lines().toList();
        assertThat(lines).hasSize(5);
        assertThat(lines.get(0).split(";")).hasSize(3);
    }

    @Test
    @DisplayName("Cells count is greater than max, rows and columns are within their limits - validation error")
    void fail_generate_with_too_many_cells() {
        sendInvalid(request(5, 3))
                .body(containsString("Can't generate cells count = [15] (rows * columns) more than max = [12]"));
        sendInvalid(request(13, 1))
                .body(containsString("Can't generate cells count = [13] (rows * columns) more than max = [12]"));
    }

    @Test
    @DisplayName("Rows or columns count is greater than max - only this error is reported")
    void fail_generate_with_too_many_rows_or_columns() {
        sendInvalid(request(21, 1))
                .body(containsString("Can't generate rows size = [21] more than max = [20]"))
                .body(not(containsString(CELLS_ERROR)));
        sendInvalid(request(3, 6))
                .body(containsString("Can't generate columns count = [6] more than max = [5]"))
                .body(not(containsString(CELLS_ERROR)));
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

    private GenerateTableRequest request(int rowsCount, int columnsCount) {
        List<ColumnDto> columns = IntStream.range(0, columnsCount)
                .mapToObj(index -> new ColumnDto()
                        .setName("column_" + index)
                        .setType(CustomType.UUID.getName())
                        .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                        .setConfig(new ConfigDto()))
                .toList();

        return new GenerateTableRequest()
                .setSize(rowsCount)
                .setFileType(FileType.CSV.name())
                .setColumns(columns);
    }
}
