package ru.akvine.istochnik.api;

import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"security.enabled=true", "metrics.enabled=true"})
@Import(SecurityEnabledTest.JwtDecoderConfig.class)
@DisplayName("OAuth 2.0 is enabled by settings")
public class SecurityEnabledTest extends ApiBaseTest {
    private static final String VALID_TOKEN = "valid-token";

    private static final String BASE_TYPES_ENDPOINT = "/v1/types/base";
    private static final String PROMETHEUS_ENDPOINT = "/actuator/prometheus";

    @TestConfiguration
    static class JwtDecoderConfig {

        // Заменяет поставщика OAuth 2.0: проверка подписи токена сводится к сравнению с единственным значением
        @Bean
        JwtDecoder jwtDecoder() {
            return token -> {
                if (!VALID_TOKEN.equals(token)) {
                    throw new BadJwtException("Token is not valid");
                }

                return Jwt.withTokenValue(token)
                        .header("alg", "none")
                        .subject("test-client")
                        .build();
            };
        }
    }

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
