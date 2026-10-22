package ru.akvine.istochnik.api.generators.faker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.http.HttpStatus;
import ru.akvine.istochnik.api.ApiBaseTest;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.enums.Topic;

// Стран на русском около двухсот, поэтому повторы при генерации выпадают постоянно
@DisplayName("Faker unique values tests")
public class GenerateUniqueByFakerTests extends ApiBaseTest {

    @Test
    @DisplayName("Generate unique values - values are not repeated")
    void successful_generate_unique_values() {
        List<String> values = generate(config(RangeType.RANDOM, Topic.COUNTRY), 150);

        assertThat(values).hasSize(150).doesNotContain("").doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("Generate unique values [notNull = false] - null is one more unique value")
    void successful_generate_unique_nullable_values() {
        List<String> values = generate(config(RangeType.RANDOM, Topic.COUNTRY).setNotNull(false), 150);

        // Пустая строка в CSV соответствует null
        assertThat(values).hasSize(150).doesNotHaveDuplicates().containsOnlyOnce("");
    }

    @Test
    @DisplayName("Generate unique values by several topics [rangeType = SHIFT] - topics are used in turn")
    void successful_generate_unique_values_by_topics_in_turn() {
        List<String> values = generate(config(RangeType.SHIFT, Topic.COUNTRY, Topic.EMAIL), 150);

        // Повтор отбрасывается, и значение строится заново тем же топиком, поэтому чередование сохраняется
        assertThat(values).hasSize(150).doesNotHaveDuplicates();
        for (int index = 0; index < values.size(); index++) {
            assertThat(values.get(index).contains("@")).isEqualTo(index % 2 == 1);
        }
    }

    @Test
    @DisplayName("Generate unique values in turn by topic with few values - generation error")
    void fail_generate_unique_values_in_turn_by_topic_with_few_values() {
        // Городов на русском меньше полусотни, а по порядку им достаётся каждая вторая из 150 строк
        sendFailed(request(config(RangeType.SHIFT, Topic.COUNTRY, Topic.CITY_NAME), 150))
                .body("message", containsString("Unique values of [faker] strategy run out"));
    }

    @Test
    @Timeout(10)
    @DisplayName("Generate more unique values than topic has - generation error instead of endless search")
    void fail_generate_more_unique_values_than_topic_has() {
        sendFailed(request(config(RangeType.RANDOM, Topic.COUNTRY), 1000))
                .body("message", containsString("Unique values of [faker] strategy run out"))
                .body("message", containsString("unique values were generated for [1000] rows"));
    }

    private ValidatableResponse sendFailed(GenerateTableRequest request) {
        return RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body("code", equalTo("generation.error"));
    }

    private List<String> generate(ConfigDto config, int size) {
        return convert(BaseType.STRING, sendGenerateRequest(request(config, size))).stream()
                .map(String::valueOf)
                .toList();
    }

    private GenerateTableRequest request(ConfigDto config, int size) {
        ColumnDto column = new ColumnDto()
                .setName("column")
                .setType(BaseType.STRING.getValue())
                .setGenerationStrategy(GenerationStrategy.FAKER.getName())
                .setConfig(config);

        return new GenerateTableRequest()
                .setSize(size)
                .setFileType(FileType.CSV.name())
                .setColumns(List.of(column));
    }

    private ConfigDto config(RangeType rangeType, Topic... topics) {
        return new ConfigDto()
                .setRangeType(rangeType.getType())
                .setNotNull(true)
                .setUnique(true)
                .setLanguage("ru")
                .setTopics(List.of(topics).stream().map(Topic::name).toList());
    }
}
