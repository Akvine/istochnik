package ru.akvine.istochnik.api.generators.algorithm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import ru.akvine.istochnik.api.ApiBaseTest;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;

@DisplayName("Unique values tests")
public class GenerateUniqueValuesTest extends ApiBaseTest {
    private static final int SIZE = 2000;

    @ParameterizedTest(name = "{0}")
    @MethodSource("uniqueColumns")
    @DisplayName("Generate unique values - values are not repeated")
    void successful_generate_unique_values(String type, ConfigDto config) {
        List<String> values = generate(type, config.setUnique(true).setNotNull(true), SIZE);

        assertThat(values).hasSize(SIZE).doesNotContain("").doesNotHaveDuplicates();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("uniqueColumns")
    @DisplayName("Generate unique values with nulls - values except null are not repeated")
    void successful_generate_unique_nullable_values(String type, ConfigDto config) {
        List<String> values = generate(type, config.setUnique(true).setNotNull(false), SIZE);

        assertThat(values).hasSize(SIZE);
        assertThat(values.stream().filter(value -> !value.isEmpty()))
                .isNotEmpty()
                .doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("Generate more unique values than the range has - generation error instead of endless search")
    void fail_generate_more_unique_values_than_range_has() {
        // В диапазоне девять целых чисел и десять секунд
        for (ColumnDto column :
                List.of(column("int", range("1", "10")), column("time", range("00:00:00", "00:00:10")))) {
            column.getConfig().setUnique(true).setNotNull(true);

            RestAssured.given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(request(column, 20))
                    .when()
                    .post(RestMethods.GENERATE_DATA_ENDPOINT)
                    .then()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body("code", equalTo("generation.error"))
                    .body("message", containsString("Max attempts to generation [100] is exceeded"));
        }
    }

    // Диапазоны немногим шире количества строк, чтобы повторы возникали часто и их приходилось отбрасывать
    static Stream<Arguments> uniqueColumns() {
        return Stream.of(
                arguments("int", range("1", "3000")),
                arguments("double", range("0", "1")),
                arguments("date", range("2000-01-01", "2008-01-01")),
                arguments("datetime", range("2025-01-01 00:00:00", "2025-01-01 01:00:00")),
                arguments("time", range("00:00:00", "01:00:00")),
                arguments("snils", requisite()),
                arguments("inn_org", requisite()),
                arguments("inn_pers", requisite()),
                arguments("ogrn", requisite()),
                arguments("imei", new ConfigDto().setValid(true)));
    }

    private static ConfigDto range(String start, String end) {
        return new ConfigDto()
                .setRangeType(RangeType.RANDOM.getType())
                .setStart(start)
                .setEnd(end);
    }

    // Диапазон на значения этих типов не влияет, но его требует проверка запроса
    private static ConfigDto requisite() {
        return range("0", "1").setValid(true);
    }

    private List<String> generate(String type, ConfigDto config, int size) {
        String file = new String(sendGenerateRequest(request(column(type, config), size)));
        // Пустая строка файла - это null
        return file.lines().skip(1).toList();
    }

    private static ColumnDto column(String type, ConfigDto config) {
        return new ColumnDto()
                .setName("column")
                .setType(type)
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(config);
    }

    private static GenerateTableRequest request(ColumnDto column, int size) {
        return new GenerateTableRequest()
                .setSize(size)
                .setFileType(FileType.CSV.name())
                .setColumns(List.of(column));
    }
}
