package ru.akvine.istochnik.api;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("OpenAPI documentation tests")
public class OpenApiDocsTest extends ApiBaseTest {

    @Test
    @DisplayName("Every endpoint is described in OpenAPI documentation")
    void every_endpoint_is_described() {
        RestAssured.given()
                .when()
                .get("/v3/api-docs")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("info.title", equalTo("Istochnik API"))
                .body(
                        "paths.keySet()",
                        containsInAnyOrder(
                                "/v1/generator",
                                "/v1/types/base",
                                "/v1/types/custom",
                                "/v1/strategies",
                                "/v1/converters",
                                "/v1/topics"))
                .body("paths.'/v1/generator'.post.summary", equalTo("Сгенерировать файл с данными"))
                .body("paths.'/v1/generator'.post.description", not(emptyOrNullString()))
                .body("paths.'/v1/generator'.post.responses", hasKey("200"))
                .body("paths.'/v1/generator'.post.responses", hasKey("400"))
                .body("paths.'/v1/generator'.post.responses", hasKey("429"))
                .body("paths.'/v1/generator'.post.responses", hasKey("500"))
                .body("paths.'/v1/generator'.post.requestBody.content.'application/json'.examples.size()", equalTo(2))
                .body("paths.'/v1/types/base'.get.summary", equalTo("Получить базовые типы"))
                .body("paths.'/v1/types/custom'.get.summary", equalTo("Получить пользовательские типы"))
                .body("paths.'/v1/strategies'.get.summary", equalTo("Получить стратегии генерации"))
                .body("paths.'/v1/converters'.get.summary", equalTo("Получить конвертеры"))
                .body("paths.'/v1/topics'.get.summary", equalTo("Получить топики"))
                .body("paths.'/v1/topics'.get.parameters[0].description", not(emptyOrNullString()));
    }

    @Test
    @DisplayName("Swagger UI is available")
    void swagger_ui_is_available() {
        RestAssured.given()
                .when()
                .get("/swagger-ui/index.html")
                .then()
                .statusCode(HttpStatus.OK.value());
    }
}
