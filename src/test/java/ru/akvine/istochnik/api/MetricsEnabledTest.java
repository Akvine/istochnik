package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.GeneratorFacade;
import ru.akvine.istochnik.services.impl.MeteredGeneratorFacade;

@TestPropertySource(properties = "metrics.enabled=true")
@DisplayName("Metrics are enabled by settings")
public class MetricsEnabledTest extends ApiBaseTest {
    private static final String PROMETHEUS_ENDPOINT = "/actuator/prometheus";

    @Autowired
    private GeneratorFacade generatorFacade;

    @Test
    @DisplayName("Prometheus endpoint returns generation metrics together with standard ones")
    void prometheus_endpoint_returns_generation_metrics() {
        sendGenerateRequest(request(7, uuidColumn()));
        sendGenerateRequest(request(3, uuidColumn()));

        // Пять уникальных значений из диапазона с одним числом: ошибка обнаруживается только при генерации
        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request(5, impossibleUniqueColumn()))
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

        String metrics = RestAssured.given()
                .when()
                .get(PROMETHEUS_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        assertThat(generatorFacade).isInstanceOf(MeteredGeneratorFacade.class);
        assertThat(metrics)
                .containsPattern(sample("istochnik_generation_seconds_count", "outcome=\"success\"", "2"))
                .containsPattern(sample("istochnik_generation_seconds_count", "outcome=\"error\"", "1"))
                // Строки запроса, завершившегося ошибкой, в счётчик не попадают
                .containsPattern(sample("istochnik_generation_rows_total", "file_type=\"csv\"", "10"))
                .contains("application=\"istochnik\"")
                .contains("jvm_memory_used_bytes")
                .contains("http_server_requests_seconds_count");
    }

    @Test
    @DisplayName("Only prometheus endpoint is exposed")
    void only_prometheus_endpoint_is_exposed() {
        RestAssured.given().when().get("/actuator").then().statusCode(HttpStatus.OK.value());
        RestAssured.given().when().get("/actuator/health").then().statusCode(HttpStatus.NOT_FOUND.value());
        RestAssured.given().when().get("/actuator/env").then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    // Строка метрики с нужной меткой и значением; порядок остальных меток и запись числа (2 или 2.0) не важны
    private String sample(String name, String label, String value) {
        return name + "\\{[^}]*" + label + "[^}]*} " + value + "(\\.0)?\\s";
    }

    private ColumnDto uuidColumn() {
        return new ColumnDto()
                .setName("id")
                .setType(CustomType.UUID.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto());
    }

    private ColumnDto impossibleUniqueColumn() {
        return new ColumnDto()
                .setName("code")
                .setType(BaseType.INTEGER.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.getType())
                        .setStart("1")
                        .setEnd("2")
                        .setUnique(true)
                        .setNotNull(true));
    }

    private GenerateTableRequest request(int size, ColumnDto column) {
        return new GenerateTableRequest()
                .setSize(size)
                .setFileType(FileType.CSV.name())
                .setColumns(List.of(column));
    }
}
