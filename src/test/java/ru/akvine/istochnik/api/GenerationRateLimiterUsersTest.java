package ru.akvine.istochnik.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.api.common.configs.TestJwtDecoderConfig;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;

@TestPropertySource(
        properties = {
            "security.enabled=true",
            "generation.rate.limiter.enabled=true",
            "generation.rate.limiter.limit-for-period=2",
            "generation.rate.limiter.refresh-period-seconds=600",
            "server.forward-headers-strategy=native"
        })
@Import(TestJwtDecoderConfig.class)
@DisplayName("Generation rate limiter tests for authenticated users")
public class GenerationRateLimiterUsersTest extends ApiBaseTest {

    @Test
    @DisplayName("Authenticated user has his own limit regardless of the address")
    void users_have_separate_limits() {
        // Адрес меняется, но пользователь тот же: лимит общий для всех его запросов
        send("valid-alice", "10.0.0.1").statusCode(HttpStatus.OK.value());
        send("valid-alice", "10.0.0.2").statusCode(HttpStatus.OK.value());
        send("valid-alice", "10.0.0.3").statusCode(HttpStatus.TOO_MANY_REQUESTS.value());

        // С того же адреса другой пользователь отправляет запросы в рамках своего лимита
        send("valid-bob", "10.0.0.1").statusCode(HttpStatus.OK.value());
        send("valid-bob", "10.0.0.1").statusCode(HttpStatus.OK.value());
        send("valid-bob", "10.0.0.1").statusCode(HttpStatus.TOO_MANY_REQUESTS.value());
    }

    private ValidatableResponse send(String user, String clientAddress) {
        return RestAssured.given()
                .auth()
                .oauth2(user)
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
