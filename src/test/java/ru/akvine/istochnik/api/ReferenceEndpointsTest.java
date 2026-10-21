package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ValidatableResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.ConverterType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.Language;
import ru.akvine.istochnik.services.MessageResolverService;

@DisplayName("Reference endpoints tests: types, strategies and converters")
public class ReferenceEndpointsTest extends ApiBaseTest {
    private static final String BASE_TYPES = "/v1/types/base";
    private static final String CUSTOM_TYPES = "/v1/types/custom";
    private static final String STRATEGIES = "/v1/strategies";
    private static final String CONVERTERS = "/v1/converters";
    private static final String TOPICS = "/v1/topics";

    private static final List<String> LANGUAGES = List.of("ru", "en");

    @Autowired
    private MessageResolverService messageResolverService;

    @Test
    @DisplayName("Base types are listed with descriptions in every language")
    void list_base_types() {
        for (String language : LANGUAGES) {
            get(BASE_TYPES, language)
                    .statusCode(HttpStatus.OK.value())
                    .body("status", equalTo("SUCCESS"))
                    .body("types.baseType", contains("int", "double", "str", "bool"))
                    // Если текста для типа нет, вместо него возвращается код сообщения
                    .body("types.description", everyItem(not(startsWith("base.type."))));
        }

        String russian = get(BASE_TYPES, "ru").extract().jsonPath().getString("types[0].description");
        String english = get(BASE_TYPES, "en").extract().jsonPath().getString("types[0].description");
        assertThat(russian).isNotBlank().isNotEqualTo(english);
    }

    @Test
    @DisplayName("Custom types are listed with base type, description and example in every language")
    void list_custom_types() {
        for (String language : LANGUAGES) {
            List<Map<String, String>> types = get(CUSTOM_TYPES, language)
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .jsonPath()
                    .getList("types");

            assertThat(types)
                    .extracting(type -> type.get("customType"))
                    .containsExactlyElementsOf(Arrays.stream(CustomType.values())
                            .map(CustomType::getName)
                            .toList());
            assertThat(types).allSatisfy(type -> {
                CustomType customType = CustomType.safeFrom(type.get("customType"));
                assertThat(type.get("baseType"))
                        .isEqualTo(customType.getBaseType().getValue());
                assertThat(type.get("example")).isEqualTo(customType.getExample());
                assertThat(type.get("description")).isNotBlank().doesNotStartWith("custom.type.");
            });
        }
    }

    @Test
    @DisplayName("Strategies are listed with supported restrictions")
    void list_strategies() {
        for (String language : LANGUAGES) {
            get(STRATEGIES, language)
                    .statusCode(HttpStatus.OK.value())
                    .body("count", equalTo(GenerationStrategy.values().length))
                    .body(
                            "strategies.name",
                            contains("constant", "algorithm", "dictionary", "regexp", "faker", "expression"))
                    .body("strategies.description", everyItem(not(containsString(".strategy.description"))))
                    .body("strategies.find { it.name == 'algorithm' }.supportsUnique", equalTo(true))
                    .body("strategies.find { it.name == 'algorithm' }.supportsValid", equalTo(true))
                    .body("strategies.find { it.name == 'constant' }.supportsNotNull", equalTo(false))
                    .body("strategies.find { it.name == 'faker' }.supportsUnique", equalTo(false));
        }
    }

    @Test
    @DisplayName("Converters are listed for every base type with a description and an example of every converter")
    @SuppressWarnings("unchecked")
    void list_converters() {
        for (String language : LANGUAGES) {
            List<Map<String, Object>> converters = listConverters(language);

            assertThat(converters).hasSameSizeAs(BaseType.values());
            for (BaseType baseType : BaseType.values()) {
                Map<String, Object> listed = converters.stream()
                        .filter(converter -> baseType.getValue().equals(converter.get("type")))
                        .findFirst()
                        .orElseThrow();

                List<Map<String, Object>> expectedNames = baseType.getSupportedConverterType().stream()
                        .map(converterType -> Map.of(
                                "name", converterType.getName(),
                                "description",
                                        messageResolverService.message(
                                                converterType.getDescriptionCode(), Language.from(language)),
                                "example", expectedExample(converterType.getExample())))
                        .toList();
                assertThat((List<Map<String, Object>>) listed.get("names"))
                        .as("%s in %s", baseType.getValue(), language)
                        .isEqualTo(expectedNames);
                assertThat(listed.get("size")).isEqualTo(expectedNames.size());
            }
        }

        // Вероятность выводится, только если пример задаёт её явно
        List<Map<String, Object>> intConverters =
                (List<Map<String, Object>>) listConverters("en").get(0).get("names");
        assertThat(example(intConverters, "to_null")).containsEntry("probability", 40);
        assertThat(example(intConverters, "abs")).doesNotContainKey("probability");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> example(List<Map<String, Object>> converters, String name) {
        return converters.stream()
                .filter(converter -> name.equals(converter.get("name")))
                .map(converter -> (Map<String, Object>) converter.get("example"))
                .findFirst()
                .orElseThrow();
    }

    @Test
    @DisplayName("Every converter has its own description in every language")
    void every_converter_has_description() {
        for (ConverterType converterType : ConverterType.values()) {
            String code = converterType.getDescriptionCode();
            String russian = messageResolverService.message(code, Language.RU);
            String english = messageResolverService.message(code, Language.EN);

            // Если текста нет, вместо него возвращается код сообщения
            assertThat(english).as(converterType.getName()).isNotEqualTo(code);
            assertThat(russian).as(converterType.getName()).isNotEqualTo(code).isNotEqualTo(english);
        }

        assertThat(Arrays.stream(ConverterType.values()).map(ConverterType::getDescriptionCode))
                .doesNotHaveDuplicates();
    }

    // Ответ читается через Jackson: в отличие от JsonPath, он возвращает дробные числа как Double,
    // и примеры можно сравнить со значениями из ConverterType
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> listConverters(String language) {
        Map<String, Object> response = get(CONVERTERS, language)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(new TypeRef<>() {});
        return (List<Map<String, Object>>) response.get("converters");
    }

    private Map<String, Object> expectedExample(ConverterType.Example example) {
        Map<String, Object> expected = new HashMap<>();
        expected.put("arguments", example.arguments());
        if (example.probability() != null) {
            expected.put("probability", example.probability());
        }
        expected.put("values", example.values());
        expected.put("results", example.results());
        return expected;
    }

    @Test
    @DisplayName("English is used when language is not set")
    void default_language_is_english() {
        for (String endpoint : List.of(BASE_TYPES, CUSTOM_TYPES, STRATEGIES, CONVERTERS, TOPICS)) {
            String withoutLanguage = RestAssured.given()
                    .when()
                    .get(endpoint)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .asString();

            assertThat(withoutLanguage).isEqualTo(get(endpoint, "en").extract().asString());
            // Регистр кода языка не важен
            assertThat(withoutLanguage).isEqualTo(get(endpoint, "EN").extract().asString());
        }
    }

    @Test
    @DisplayName("Unsupported language - error")
    void fail_list_with_unsupported_language() {
        for (String endpoint : List.of(BASE_TYPES, CUSTOM_TYPES, STRATEGIES, CONVERTERS, TOPICS)) {
            get(endpoint, "de")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("status", equalTo("FAIL"))
                    .body("code", equalTo("language.notSupported.error"))
                    .body("message", equalTo("Language = [de] is not supported by app!"));
        }
    }

    private ValidatableResponse get(String endpoint, String language) {
        return RestAssured.given()
                .queryParam("lang", language)
                .when()
                .get(endpoint)
                .then();
    }
}
