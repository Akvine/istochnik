package ru.akvine.istochnik.api;

import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import ru.akvine.istochnik.api.common.configs.TestJwtDecoderConfig;

@TestPropertySource(properties = {"security.enabled=true", "metrics.enabled=true"})
@Import(TestJwtDecoderConfig.class)
@DisplayName("OAuth 2.0 is enabled by settings")
public class SecurityEnabledTest extends ApiBaseTest {
    private static final String VALID_TOKEN = "valid-token";

    private static final String BASE_TYPES_ENDPOINT = "/v1/types/base";
    private static final String PROMETHEUS_ENDPOINT = "/actuator/prometheus";

    @Test
    @DisplayName("Request without token is rejected with 401")
    void request_without_token_is_rejected() {
        RestAssured.given().when().get(BASE_TYPES_ENDPOINT).then().statusCode(HttpStatus.UNAUTHORIZED.value());
        RestAssured.given().when().get("/v3/api-docs").then().statusCode(HttpStatus.UNAUTHORIZED.value());
        // Метрики закрыты так же, как и остальные методы
        RestAssured.given().when().get(PROMETHEUS_ENDPOINT).then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Request with invalid token is rejected with 401")
    void request_with_invalid_token_is_rejected() {
        RestAssured.given()
                .auth()
                .oauth2("another-token")
                .when()
                .get(BASE_TYPES_ENDPOINT)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Request with valid token is accepted")
    void request_with_valid_token_is_accepted() {
        RestAssured.given()
                .auth()
                .oauth2(VALID_TOKEN)
                .when()
                .get(BASE_TYPES_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("status", equalTo("SUCCESS"));

        RestAssured.given()
                .auth()
                .oauth2(VALID_TOKEN)
                .when()
                .get(PROMETHEUS_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value());
    }
}
