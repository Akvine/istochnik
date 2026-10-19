package ru.akvine.istochnik.api.generators.dictionary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
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

@DisplayName("Dictionary with weights tests")
public class GenerateByWeightedDictionaryTests extends ApiBaseTest {
    private static final int SIZE = 10000;
    // Допустимое отклонение доли значения от ожидаемой
    private static final double TOLERANCE = 0.02;
    private static final String NULL = "";

    @Test
    @DisplayName("Values are chosen according to their weights")
    void successful_generate_by_weights() {
        Map<String, Double> shares = shares(generate(config(
                Set.of(Set.of("NEW", "ACTIVE", "BLOCKED")), Map.of("NEW", 70.0, "ACTIVE", 25.0, "BLOCKED", 5.0))));

        assertThat(shares.get("NEW")).isCloseTo(0.70, within(TOLERANCE));
        assertThat(shares.get("ACTIVE")).isCloseTo(0.25, within(TOLERANCE));
        assertThat(shares.get("BLOCKED")).isCloseTo(0.05, within(TOLERANCE));
    }

    @Test
    @DisplayName("Value with zero weight is never chosen, fractional weights are allowed")
    void successful_generate_without_zero_weight_values() {
        Map<String, Double> shares =
                shares(generate(config(Set.of(Set.of("A", "B", "C")), Map.of("A", 0.5, "B", 0.0, "C", 1.5))));

        assertThat(shares).doesNotContainKey("B");
        assertThat(shares.get("A")).isCloseTo(0.25, within(TOLERANCE));
        assertThat(shares.get("C")).isCloseTo(0.75, within(TOLERANCE));
    }

    @Test
    @DisplayName("Values without weight get weight 1")
    void successful_generate_with_default_weight() {
        Map<String, Double> shares = shares(generate(config(Set.of(Set.of("A", "B", "C", "D")), Map.of("A", 7.0))));

        assertThat(shares.get("A")).isCloseTo(0.7, within(TOLERANCE));
        assertThat(shares.get("B")).isCloseTo(0.1, within(TOLERANCE));
        assertThat(shares.get("C")).isCloseTo(0.1, within(TOLERANCE));
        assertThat(shares.get("D")).isCloseTo(0.1, within(TOLERANCE));
    }

    @Test
    @DisplayName("Value from several dictionaries has one weight")
    void successful_generate_by_several_dictionaries() {
        Map<String, Double> shares =
                shares(generate(config(Set.of(Set.of("A", "B"), Set.of("B", "C")), Map.of("B", 8.0))));

        assertThat(shares.get("A")).isCloseTo(0.1, within(TOLERANCE));
        assertThat(shares.get("B")).isCloseTo(0.8, within(TOLERANCE));
        assertThat(shares.get("C")).isCloseTo(0.1, within(TOLERANCE));
    }

    @Test
    @DisplayName("Column with nulls - weights apply to values except null")
    void successful_generate_nullable_by_weights() {
        List<String> values = generate(
                config(Set.of(Set.of("A", "B")), Map.of("A", 9.0, "B", 1.0)).setNotNull(false));

        Map<String, Double> shares = shares(values);
        assertThat(shares.get(NULL)).isCloseTo(0.5, within(TOLERANCE));
        assertThat(shares(values.stream().filter(value -> !NULL.equals(value)).toList())
                        .get("A"))
                .isCloseTo(0.9, within(TOLERANCE));
    }

    @Test
    @DisplayName("Without weights and with empty weights values are the same as before weights appeared")
    void successful_generate_without_weights() {
        Set<Set<String>> dictionaries = Set.of(Set.of("NEW", "ACTIVE", "BLOCKED"));
        List<String> withoutWeights = generate(config(dictionaries, null));

        assertThat(generate(config(dictionaries, Map.of()))).isEqualTo(withoutWeights);
        assertThat(shares(withoutWeights).values())
                .allSatisfy(share -> assertThat(share).isCloseTo(1.0 / 3, within(TOLERANCE)));
    }

    @Test
    @DisplayName("Invalid weights - validation errors")
    void fail_generate_with_invalid_weights() {
        Set<Set<String>> dictionaries = Set.of(Set.of("A", "B"));

        sendInvalid(config(dictionaries, Map.of("A", 1.0, "X", 2.0)))
                .body(
                        "columnErrors.column",
                        hasItem("field [weights] contains values which are absent in dictionaries: [X]"));
        sendInvalid(config(dictionaries, Map.of("A", -1.0)))
                .body(
                        "columnErrors.column",
                        hasItem("weights must be finite numbers not less than 0, invalid weights of: [A]"));
        sendInvalid(config(dictionaries, Map.of("A", 0.0, "B", 0.0)))
                .body("columnErrors.column", hasItem("at least one dictionary value must have weight greater than 0"));
        sendInvalid(config(dictionaries, Map.of("A", Double.MAX_VALUE, "B", Double.MAX_VALUE)))
                .body("columnErrors.column", hasItem("sum of weights is too large"));
    }

    @Test
    @DisplayName("Weights with shift or unique values - validation errors")
    void fail_generate_with_weights_and_shift_or_unique() {
        Set<Set<String>> dictionaries = Set.of(Set.of("A", "B", "C"));

        sendInvalid(config(dictionaries, Map.of("A", 2.0)).setRangeType(RangeType.SHIFT.getType()))
                .body("columnErrors.column", hasItem("field [weights] can be used only with rangeType = [random]"));
        sendInvalid(config(dictionaries, Map.of("A", 2.0)).setUnique(true))
                .body("columnErrors.column", hasItem("field [weights] can't be used together with unique = true"));
    }

    private Map<String, Double> shares(List<String> values) {
        return values.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> (double) entry.getValue() / values.size()));
    }

    private List<String> generate(ConfigDto config) {
        byte[] file = sendGenerateRequest(request(config));
        // Пустая строка файла - это null
        return new String(file, StandardCharsets.UTF_8).lines().skip(1).toList();
    }

    private ValidatableResponse sendInvalid(ConfigDto config) {
        return RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request(config))
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("code", equalTo("request.fields.invalid.error"));
    }

    private ConfigDto config(Set<Set<String>> dictionaries, Map<String, Double> weights) {
        return new ConfigDto()
                .setRangeType(RangeType.RANDOM.getType())
                .setNotNull(true)
                .setDictionaries(dictionaries)
                .setWeights(weights);
    }

    private GenerateTableRequest request(ConfigDto config) {
        ColumnDto column = new ColumnDto()
                .setName("column")
                .setType(BaseType.STRING.getValue())
                .setGenerationStrategy(GenerationStrategy.DICTIONARY.getName())
                .setConfig(config);

        return new GenerateTableRequest()
                .setSize(SIZE)
                .setFileType(FileType.CSV.name())
                .setColumns(List.of(column));
    }
}
