package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;

// Очередь пула в общих настройках тестов вмещает одну задачу, поэтому для запроса с несколькими колонками она увеличена
@TestPropertySource(
        properties = {
            "max.generation.columns.size=5",
            "parallel.executor.threads-count=2",
            "parallel.executor.queue-capacity=10"
        })
@DisplayName("Columns count limit tests")
public class GenerationColumnsLimitTest extends ApiBaseTest {
    private static final int MAX_COLUMNS = 5;

    @Test
    @DisplayName("Columns count equals to max - successfully")
    void successful_generate_with_max_columns_count() {
        byte[] response = sendGenerateRequest(request(columns(MAX_COLUMNS)));

        String header = new String(response).lines().findFirst().orElseThrow();
        assertThat(header.split(";")).hasSize(MAX_COLUMNS);
    }

    @Test
    @DisplayName("Columns count is greater than max - validation error")
    void fail_generate_with_too_many_columns() {
        sendInvalid(
                request(columns(MAX_COLUMNS + 1)),
                "Can't generate columns count = [" + (MAX_COLUMNS + 1) + "] more than max = [" + MAX_COLUMNS + "]");
    }

    @Test
    @DisplayName("Columns are empty or absent - validation error")
    void fail_generate_without_columns() {
        sendInvalid(request(List.of()), "Field 'columns' can't be empty");
        sendInvalid(request(null), "Field 'columns' can't be empty");
    }

    private void sendInvalid(GenerateTableRequest request, String expectedMessage) {
        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(containsString(expectedMessage));
    }

    private List<ColumnDto> columns(int count) {
        return IntStream.range(0, count)
                .mapToObj(index -> new ColumnDto()
                        .setName("column_" + index)
                        .setType(CustomType.UUID.getName())
                        .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                        .setConfig(new ConfigDto()))
                .toList();
    }

    private GenerateTableRequest request(List<ColumnDto> columns) {
        return new GenerateTableRequest()
                .setSize(2)
                .setFileType(FileType.CSV.name())
                .setColumns(columns);
    }
}
