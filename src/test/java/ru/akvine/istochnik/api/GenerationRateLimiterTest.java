package ru.akvine.istochnik.api;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;

// Период заведомо длиннее теста, чтобы лимит не успел восстановиться.
// Без аутентификации пользователь определяется по адресу клиента: тест подставляет его заголовком X-Forwarded-For,
// как это делает прокси перед сервисом
@TestPropertySource(
        properties = {
            "generation.rate.limiter.enabled=true",
            "generation.rate.limiter.limit-for-period=3",
            "generation.rate.limiter.refresh-period-seconds=600",
            "server.forward-headers-strategy=native"
        })
@DisplayName("Generation rate limiter tests")
public class GenerationRateLimiterTest extends ApiBaseTest {

    @Test
    @DisplayName("Requests over the limit are rejected with 429, other endpoints are not limited")
    void requests_over_limit_are_rejected() {
        for (int i = 0; i < 3; i++) {
            send("10.0.0.1").statusCode(HttpStatus.OK.value());
        }

        send("10.0.0.1")
                .statusCode(HttpStatus.TOO_MANY_REQUESTS.value())
                .header(HttpHeaders.RETRY_AFTER, equalTo("600"))
                .body("code", equalTo("rate.limit.exceeded.error"))
                .body("message", containsString("Limit = [3] requests per [600] seconds"));

        // Справочные методы лимитом не ограничены
        RestAssured.given()
                .header("X-Forwarded-For", "10.0.0.1")
                .when()
                .get("/v1/types/base")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Every client has its own limit")
    void clients_have_separate_limits() {
        for (int i = 0; i < 3; i++) {
            send("10.0.0.2").statusCode(HttpStatus.OK.value());
        }
        send("10.0.0.2").statusCode(HttpStatus.TOO_MANY_REQUESTS.value());

        // Исчерпанный лимит одного клиента не мешает другому
        for (int i = 0; i < 3; i++) {
            send("10.0.0.3").statusCode(HttpStatus.OK.value());
        }
        send("10.0.0.3").statusCode(HttpStatus.TOO_MANY_REQUESTS.value());
    }

    private ValidatableResponse send(String clientAddress) {
        return RestAssured.given()
                .header("X-Forwarded-For", clientAddress)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request())
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then();
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
