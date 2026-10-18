package ru.akvine.istochnik.api.generators.faker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

@DisplayName("Faker topics tests: text and coordinates")
public class FakerTopicsTest extends ApiBaseTest {
    private static final String COLUMN = "value";
    private static final int SIZE = 50;
    private static final int MAX_TEXT_LENGTH = 1000;

    private static final Pattern CYRILLIC = Pattern.compile("[А-Яа-яЁё]");
    private static final Pattern DEGREES = Pattern.compile("-?\\d{1,3}\\.\\d{6}");
    private static final String COORDINATES_SEPARATOR = ", ";

    @Test
    @DisplayName("Generate TEXT topic - text has exactly the given length")
    void successful_generate_text() {
        for (int length : List.of(1, 2, 37, 255, MAX_TEXT_LENGTH)) {
            List<String> texts = generate(config(null, Topic.TEXT).setLength(length));

            assertThat(texts).hasSize(SIZE).allMatch(text -> text.length() == length);
            assertThat(texts).allMatch(text -> Character.isUpperCase(text.charAt(0)));
            // Пробел в конце мог бы потеряться при записи значения, и длина стала бы меньше заданной
            assertThat(texts).noneMatch(text -> text.endsWith(" "));
        }

        List<String> sentences = generate(config(null, Topic.TEXT).setLength(255));
        assertThat(sentences).allMatch(text -> text.contains(" ") && text.contains("."));
        assertThat(new HashSet<>(sentences)).hasSize(SIZE);
    }

    @Test
    @DisplayName("Generate TEXT topic without length - default length is used")
    void successful_generate_text_with_default_length() {
        assertThat(generate(config(null, Topic.TEXT))).hasSize(SIZE).allMatch(text -> text.length() == 100);
        // Язык колонки на текст не влияет
        assertThat(generate(config("ru", Topic.TEXT)))
                .hasSize(SIZE)
                .allMatch(text -> text.length() == 100 && !hasCyrillic(text));
    }

    @Test
    @DisplayName("Generate TEXT with another topic - length is applied to text only")
    void successful_generate_text_with_another_topic() {
        ConfigDto config = config("ru", Topic.TEXT, Topic.CITY_NAME)
                .setRangeType(RangeType.SHIFT.getType())
                .setLength(300);

        List<String> values = generate(config);

        for (int index = 0; index < values.size(); index++) {
            if (index % 2 == 0) {
                assertThat(values.get(index)).hasSize(300);
            } else {
                assertThat(values.get(index)).hasSizeLessThan(300).matches(this::hasCyrillic);
            }
        }
    }

    @Test
    @DisplayName("Generate COORDINATES topic - latitude and longitude with dot as decimal separator in any language")
    void successful_generate_coordinates() {
        for (String language : Arrays.asList(null, "en", "ru")) {
            List<String> coordinates = generate(config(language, Topic.COORDINATES));

            assertThat(coordinates).hasSize(SIZE).allSatisfy(value -> {
                String[] parts = value.split(COORDINATES_SEPARATOR);
                assertThat(parts).hasSize(2);
                assertThat(isDegrees(parts[0], 90)).isTrue();
                assertThat(isDegrees(parts[1], 180)).isTrue();
            });
            assertThat(new HashSet<>(coordinates)).hasSize(SIZE);
            // Долгота занимает весь свой диапазон, а не только диапазон широты
            assertThat(coordinates).anyMatch(value -> Math.abs(longitude(value)) > 90);
        }
    }

    @Test
    @DisplayName("Values of new topics can be null and are reproducible by seed")
    void successful_generate_nullable_and_reproducible() {
        ConfigDto nullable = config("ru", Topic.TEXT, Topic.COORDINATES).setNotNull(false);

        List<String> first = generate(nullable);
        List<String> second = generate(nullable);

        assertThat(first).contains("").anyMatch(value -> !value.isEmpty());
        assertThat(second).isEqualTo(first);
    }

    @Test
    @DisplayName("Invalid faker parameters - validation error instead of generation failure")
    void fail_generate_with_invalid_parameters() {
        sendInvalid(new ConfigDto(), "field [topics] can't be empty");
        sendInvalid(null, "field [config] can't be null");
        sendInvalid(
                new ConfigDto().setTopics(List.of("EMAIL", "UNKNOWN")), "Topic = [UNKNOWN] is not supported by app!");
        sendInvalid(new ConfigDto().setTopics(List.of(" ")), "Value is blank!");

        String lengthError = "field [length] for topic [TEXT] must be between 1 and max = [" + MAX_TEXT_LENGTH + "]";
        sendInvalid(config(null, Topic.TEXT).setLength(0), lengthError);
        sendInvalid(config(null, Topic.TEXT).setLength(-5), lengthError);
        sendInvalid(config(null, Topic.TEXT).setLength(MAX_TEXT_LENGTH + 1), lengthError);
        sendInvalid(config(null, Topic.EMAIL, Topic.TEXT).setLength(MAX_TEXT_LENGTH + 1), lengthError);

        // Остальным топикам длина не нужна, поэтому и не проверяется
        assertThat(generate(config(null, Topic.EMAIL).setLength(MAX_TEXT_LENGTH + 1)))
                .hasSize(SIZE);
    }

    @Test
    @DisplayName("Every topic has description and example in every language")
    void every_topic_is_described() {
        for (String language : List.of("ru", "en")) {
            List<Map<String, String>> topics = RestAssured.given()
                    .queryParam("lang", language)
                    .when()
                    .get("/v1/topics")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .jsonPath()
                    .getList("topics");

            assertThat(topics)
                    .extracting(topic -> topic.get("name"))
                    .containsExactlyElementsOf(Arrays.stream(Topic.values())
                            .map(topic -> topic.name().toLowerCase())
                            .toList());
            // Если текста для топика нет, вместо него возвращается код сообщения
            assertThat(topics).allSatisfy(topic -> {
                assertThat(topic.get("description")).isNotBlank().doesNotStartWith("topic.");
                assertThat(topic.get("example")).isNotBlank().doesNotStartWith("topic.");
            });
        }
    }

    private List<String> generate(ConfigDto config) {
        List<?> values = convert(BaseType.STRING, sendGenerateRequest(request(config)));
        return values.stream().map(String::valueOf).toList();
    }

    private void sendInvalid(ConfigDto config, String expectedMessage) {
        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request(config))
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("columnErrors." + COLUMN, hasItem(containsString(expectedMessage)));
    }

    private GenerateTableRequest request(ConfigDto config) {
        return new GenerateTableRequest()
                .setSize(SIZE)
                .setFileType(FileType.CSV.name())
                .setColumns(List.of(new ColumnDto()
                        .setName(COLUMN)
                        .setType(BaseType.STRING.getValue())
                        .setGenerationStrategy(GenerationStrategy.FAKER.getName())
                        .setConfig(config)));
    }

    private ConfigDto config(String language, Topic... topics) {
        return new ConfigDto()
                .setRangeType(RangeType.RANDOM.getType())
                .setNotNull(true)
                .setLanguage(language)
                .setTopics(Arrays.stream(topics).map(Topic::name).toList());
    }

    private boolean hasCyrillic(String value) {
        return CYRILLIC.matcher(value).find();
    }

    private boolean isDegrees(String value, int max) {
        return DEGREES.matcher(value).matches() && Math.abs(Double.parseDouble(value)) <= max;
    }

    private double longitude(String coordinates) {
        return Double.parseDouble(coordinates.split(COORDINATES_SEPARATOR)[1]);
    }
}
