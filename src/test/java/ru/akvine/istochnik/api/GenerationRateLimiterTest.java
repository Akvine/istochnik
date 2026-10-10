package ru.akvine.istochnik.api;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;

// Период заведомо длиннее теста, чтобы лимит не успел восстановиться
@TestPropertySource(
        properties = {
            "generation.rate.limiter.enabled=true",
            "generation.rate.limiter.limit-for-period=3",
            "generation.rate.limiter.refresh-period-seconds=600"
        })
@DisplayName("Generation rate limiter tests")
public class GenerationRateLimiterTest extends ApiBaseTest {

    @Test
    @DisplayName("Requests over the limit are rejected with 429, other endpoints are not limited")
    void requests_over_limit_are_rejected() {
        for (int i = 0; i < 3; i++) {
            sendGenerateRequest(request());
        }

        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request())
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.TOO_MANY_REQUESTS.value())
                .header(HttpHeaders.RETRY_AFTER, equalTo("600"))
                .body("code", equalTo("rate.limit.exceeded.error"))
                .body("message", containsString("Limit = [3] requests per [600] seconds"));

        // Справочные методы лимитом не ограничены
        RestAssured.given().when().get("/v1/types/base").then().statusCode(HttpStatus.OK.value());
    }

    private GenerateTableRequest request() {
        ColumnDto column = new ColumnDto()
                .setName("column")
                .setType(CustomType.UUID.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto());

        return new GenerateTableRequest()
                .setSize(1)
                .setFileType(FileType.CSV.name())
                .setColumns(List.of(column));
    }
}
