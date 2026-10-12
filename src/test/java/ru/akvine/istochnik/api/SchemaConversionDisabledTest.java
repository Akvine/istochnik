package ru.akvine.istochnik.api;

import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import ru.akvine.istochnik.api.common.configs.RestMethods;

@TestPropertySource(properties = "schema.conversion.enabled=false")
@DisplayName("Schema conversion is disabled by settings")
public class SchemaConversionDisabledTest extends ApiBaseTest {

    @Test
    @DisplayName("Conversion request is rejected, even an invalid one")
    void fail_convert_when_disabled() {
        String schema = "CREATE TABLE users (id bigint PRIMARY KEY);";

        RestAssured.given()
                .multiPart("file", "V1__init.sql", schema.getBytes(StandardCharsets.UTF_8))
                .accept(ContentType.JSON)
                .when()
                .post(RestMethods.CONVERT_SCHEMA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("status", equalTo("FAIL"))
                .body("code", equalTo("schema.conversion.disabled.error"))
                .body("message", equalTo("Schema conversion is disabled by app settings"));

        // Запрос без файла тоже получает отказ, а не подсказку о том, чего в нём не хватает
        RestAssured.given()
                .multiPart("other", "x.txt", schema.getBytes(StandardCharsets.UTF_8))
                .accept(ContentType.JSON)
                .when()
                .post(RestMethods.CONVERT_SCHEMA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("code", equalTo("schema.conversion.disabled.error"));
    }

    @Test
    @DisplayName("Other endpoints work as before")
    void successful_other_endpoints_when_disabled() {
        RestAssured.given()
                .when()
                .get("/v1/strategies")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("status", equalTo("SUCCESS"));
    }
}
