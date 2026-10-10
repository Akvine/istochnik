package ru.akvine.istochnik.api.generators.expression;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.api.ApiBaseTest;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;

// Очередь пула в общих настройках тестов вмещает одну задачу, а в запросе две колонки
@TestPropertySource(
        properties = {
            "generation.expression.enabled=false",
            "parallel.executor.threads-count=2",
            "parallel.executor.queue-capacity=10"
        })
@DisplayName("Disabled expression strategy tests")
public class ExpressionStrategyDisabledTest extends ApiBaseTest {

    @Test
    @DisplayName("Request with expression column is rejected with validation error")
    void fail_generate_with_expression_column() {
        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request(constant("price"), expression("total", "#price * 2")))
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(
                        "columnErrors.total",
                        hasItem(containsString("generation strategy [expression] is disabled by app settings")));
    }

    @Test
    @DisplayName("Other strategies work as before")
    void successful_generate_without_expression_columns() {
        byte[] response = sendGenerateRequest(request(constant("price"), constant("qty")));

        assertThat(new String(response).lines()).containsExactly("price;qty", "10;10", "10;10");
    }

    @Test
    @DisplayName("Disabled strategy is not listed among strategies")
    void strategy_is_not_listed() {
        RestAssured.given()
                .when()
                .get("/v1/strategies")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("strategies.name", not(hasItem(GenerationStrategy.EXPRESSION.getName())))
                .body(
                        "strategies.name",
                        hasItems(
                                GenerationStrategy.CONSTANT.getName(),
                                GenerationStrategy.ALGORITHM.getName(),
                                GenerationStrategy.DICTIONARY.getName(),
                                GenerationStrategy.REGEXP.getName(),
                                GenerationStrategy.FAKER.getName()));
    }

    private ColumnDto constant(String name) {
        return new ColumnDto()
                .setName(name)
                .setType(BaseType.INTEGER.getValue())
                .setGenerationStrategy(GenerationStrategy.CONSTANT.getName())
                .setConfig(new ConfigDto().setConstant(10));
    }

    private ColumnDto expression(String name, String expression) {
        return new ColumnDto()
                .setName(name)
                .setType(BaseType.INTEGER.getValue())
                .setGenerationStrategy(GenerationStrategy.EXPRESSION.getName())
                .setConfig(new ConfigDto().setExpression(expression));
    }

    private GenerateTableRequest request(ColumnDto... columns) {
        return new GenerateTableRequest()
                .setSize(2)
                .setFileType(FileType.CSV.name())
                .setColumns(List.of(columns));
    }
}
