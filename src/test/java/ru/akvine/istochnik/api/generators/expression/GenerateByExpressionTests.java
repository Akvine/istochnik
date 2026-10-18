package ru.akvine.istochnik.api.generators.expression;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import ru.akvine.istochnik.api.ApiBaseTest;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.controllers.dto.ErrorResolveInfo;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.controllers.dto.converters.ConverterDto;
import ru.akvine.istochnik.enums.*;

// Очередь пула в общих настройках тестов вмещает одну задачу, а в этих запросах по несколько колонок.
// Лимит длины результата уменьшен, чтобы его можно было превысить коротким выражением
@TestPropertySource(
        properties = {
            "parallel.executor.threads-count=4",
            "parallel.executor.queue-capacity=100",
            "generation.expression.max-result-length=60"
        })
@DisplayName("Expression strategy tests")
public class GenerateByExpressionTests extends ApiBaseTest {
    private static final int SIZE = 40;

    @Test
    @DisplayName("Arithmetic over columns of the same row")
    void successful_calculate_arithmetic() {
        Map<String, List<String>> table = generate(
                sequence("price"),
                constant("qty", 3),
                expression("total", BaseType.INTEGER, "#price * #qty"),
                expression("half", BaseType.DOUBLE, "#price / 2.0"),
                expression("expensive", BaseType.BOOLEAN, "#total > 30"));

        assertThat(table.get("price")).hasSize(SIZE);
        for (int row = 0; row < SIZE; row++) {
            long price = Long.parseLong(table.get("price").get(row));

            assertThat(Long.parseLong(table.get("total").get(row))).isEqualTo(price * 3);
            assertThat(Double.parseDouble(table.get("half").get(row))).isEqualTo(price / 2.0);
            assertThat(Boolean.parseBoolean(table.get("expensive").get(row))).isEqualTo(price * 3 > 30);
        }
    }

    @Test
    @DisplayName("Decimal columns take part in expressions as exact numbers")
    void successful_calculate_with_decimal() {
        ColumnDto price = new ColumnDto()
                .setName("price")
                .setType(CustomType.DECIMAL.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.getType())
                        .setStart("0.01")
                        .setEnd("1000.00")
                        .setNotNull(true));

        Map<String, List<String>> table = generate(
                price,
                constant("qty", 3),
                expression("total", BaseType.STRING, "#price * #qty"),
                expression("discounted", BaseType.STRING, "#round(#price * 0.9, 2)"),
                expression("rounded", BaseType.INTEGER, "#price"));

        for (int row = 0; row < SIZE; row++) {
            BigDecimal value = new BigDecimal(table.get("price").get(row));

            assertThat(table.get("total").get(row))
                    .isEqualTo(value.multiply(BigDecimal.valueOf(3)).toPlainString());
            assertThat(table.get("discounted").get(row))
                    .isEqualTo(value.multiply(new BigDecimal("0.9"))
                            .setScale(2, RoundingMode.HALF_UP)
                            .toPlainString());
            assertThat(table.get("rounded").get(row))
                    .isEqualTo(value.setScale(0, RoundingMode.HALF_UP).toPlainString());
        }
    }

    @Test
    @DisplayName("Strings are built from other columns, converters are applied to the result")
    void successful_calculate_strings() {
        Map<String, List<String>> table = generate(
                dictionary("first", true, "Иван", "Пётр", "Анна"),
                dictionary("last", true, "Иванов", "Петров", "Сидорова", "Кузнецова"),
                expression("login", BaseType.STRING, "#lower(#substring(#first, 0, 1) + '.' + #last)"),
                expression("full_name", BaseType.STRING, "#last + ' ' + #first")
                        .setConverters(List.of(new ConverterDto().setName(ConverterType.UPPER_CASE.getName()))),
                expression("name_length", BaseType.INTEGER, "#length(#first) + #length(#last)"));

        for (int row = 0; row < SIZE; row++) {
            String first = table.get("first").get(row);
            String last = table.get("last").get(row);

            assertThat(table.get("login").get(row)).isEqualTo((first.charAt(0) + "." + last).toLowerCase());
            assertThat(table.get("full_name").get(row)).isEqualTo((last + " " + first).toUpperCase());
            assertThat(table.get("name_length").get(row)).isEqualTo(String.valueOf(first.length() + last.length()));
        }
    }

    @Test
    @DisplayName("Dates are shifted and compared")
    void successful_calculate_dates() {
        ColumnDto start = new ColumnDto()
                .setName("start_date")
                .setType(CustomType.DATE.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.getType())
                        .setStart("2025-01-01")
                        .setEnd("2025-12-31")
                        .setNotNull(true));

        Map<String, List<String>> table = generate(
                start,
                sequence("duration"),
                expression("end_date", BaseType.STRING, "#plusDays(#start_date, #duration)"),
                expression("is_later", BaseType.BOOLEAN, "#end_date > #start_date"),
                expression("days", BaseType.INTEGER, "#daysBetween(#start_date, #end_date)"));

        for (int row = 0; row < SIZE; row++) {
            LocalDate startDate = LocalDate.parse(table.get("start_date").get(row));
            long duration = Long.parseLong(table.get("duration").get(row));

            assertThat(table.get("end_date").get(row))
                    .isEqualTo(startDate.plusDays(duration).toString());
            assertThat(table.get("is_later").get(row)).isEqualTo(String.valueOf(duration > 0));
            assertThat(table.get("days").get(row)).isEqualTo(String.valueOf(duration));
        }
    }

    @Test
    @DisplayName("Calculated columns depend on each other - calculation order doesn't depend on the request order")
    void successful_calculate_chain() {
        Map<String, List<String>> table = generate(
                expression("label", BaseType.STRING, "'row-' + #third"),
                expression("third", BaseType.INTEGER, "#second + 1"),
                expression("second", BaseType.INTEGER, "#first * 2"),
                sequence("first"),
                expression("independent", BaseType.INTEGER, "2 + 2"));

        // Колонки в файле идут в порядке запроса
        assertThat(table.keySet()).containsExactly("label", "third", "second", "first", "independent");
        for (int row = 0; row < SIZE; row++) {
            long first = Long.parseLong(table.get("first").get(row));

            assertThat(table.get("second").get(row)).isEqualTo(String.valueOf(first * 2));
            assertThat(table.get("third").get(row)).isEqualTo(String.valueOf(first * 2 + 1));
            assertThat(table.get("label").get(row)).isEqualTo("row-" + (first * 2 + 1));
            assertThat(table.get("independent").get(row)).isEqualTo("4");
        }
    }

    @Test
    @DisplayName("Null in a referenced column gives null, with notNull the expression handles null itself")
    void successful_calculate_with_nulls() {
        Map<String, List<String>> table = generate(
                dictionary("name", false, "Иван", "Пётр", "Анна"),
                sequence("id"),
                expression("greeting", BaseType.STRING, "'Привет, ' + #name"),
                expression("score", BaseType.INTEGER, "#id * #length(#name)"),
                notNull(expression("safe_name", BaseType.STRING, "#name ?: 'unknown'")),
                notNull(expression("name_length", BaseType.INTEGER, "#name == null ? 0 : #length(#name)")));

        // В CSV null записывается пустым значением
        assertThat(table.get("name")).contains("").anyMatch(name -> !name.isEmpty());
        for (int row = 0; row < SIZE; row++) {
            String name = table.get("name").get(row);
            long id = Long.parseLong(table.get("id").get(row));

            if (name.isEmpty()) {
                assertThat(table.get("greeting").get(row)).isEmpty();
                assertThat(table.get("score").get(row)).isEmpty();
                assertThat(table.get("safe_name").get(row)).isEqualTo("unknown");
                assertThat(table.get("name_length").get(row)).isEqualTo("0");
            } else {
                assertThat(table.get("greeting").get(row)).isEqualTo("Привет, " + name);
                assertThat(table.get("score").get(row)).isEqualTo(String.valueOf(id * name.length()));
                assertThat(table.get("safe_name").get(row)).isEqualTo(name);
                assertThat(table.get("name_length").get(row)).isEqualTo(String.valueOf(name.length()));
            }
        }
    }

    @Test
    @DisplayName("Columns with any names are referenced through the row")
    void successful_calculate_with_row_access() {
        Map<String, List<String>> table = generate(
                dictionary("first name", true, "Иван", "Пётр", "Анна"),
                // Имя колонки совпадает с именем функции
                dictionary("lower", true, "a", "b"),
                expression("result", BaseType.STRING, "#row['first name'] + '-' + #upper(#row['lower'])"));

        for (int row = 0; row < SIZE; row++) {
            assertThat(table.get("result").get(row))
                    .isEqualTo(table.get("first name").get(row) + "-"
                            + table.get("lower").get(row).toUpperCase());
        }
    }

    @Test
    @DisplayName("Calculated values keep their types in SQL")
    void successful_calculate_to_sql() {
        GenerateTableRequest request = request(
                        sequence("id"),
                        expression("doubled", BaseType.INTEGER, "#id * 2"),
                        expression("ratio", BaseType.DOUBLE, "#id / 4.0"),
                        expression("label", BaseType.STRING, "'item-' + #id"),
                        expression("odd", BaseType.BOOLEAN, "#id % 2 == 1"),
                        expression("empty", BaseType.STRING, "null"))
                .setSize(2)
                .setFileType(FileType.SQL.name())
                .setTableName("items");

        String sql = new String(sendGenerateRequest(request), StandardCharsets.UTF_8);

        assertThat(sql)
                .isEqualTo("INSERT INTO items (ID, DOUBLED, RATIO, LABEL, ODD, EMPTY) VALUES\n"
                        + "(1, 2, 0.25, 'item-1', TRUE, NULL),\n"
                        + "(2, 4, 0.5, 'item-2', FALSE, NULL);\n");
    }

    @Test
    @DisplayName("Calculated values of all types are written to XLSX")
    void successful_calculate_to_xlsx() {
        GenerateTableRequest request = request(
                        sequence("id"),
                        expression("doubled", BaseType.INTEGER, "#id * 2"),
                        expression("ratio", BaseType.DOUBLE, "#id / 4.0"),
                        expression("label", BaseType.STRING, "'item-' + #id"),
                        expression("odd", BaseType.BOOLEAN, "#id % 2 == 1"))
                .setFileType(FileType.XLSX.name());

        assertThat(sendGenerateRequest(request)).isNotEmpty();
    }

    @Test
    @DisplayName("Error in expression interrupts the request or is replaced by default value")
    void calculation_error_follows_error_policy() {
        sendFailed(
                request(sequence("id"), expression("broken", BaseType.INTEGER, "#id / 0")),
                "Error while generate column with name = [broken]: can't calculate expression for row [1]: / by zero");

        ColumnDto ignored = expression("broken", BaseType.INTEGER, "#id / 0")
                .setErrorResolveInfo(new ErrorResolveInfo()
                        .setPolicy(ErrorResolvePolicy.IGNORE)
                        .setDefaultValue("N/A"));
        Map<String, List<String>> table =
                generate(sequence("id"), ignored, expression("dependent", BaseType.STRING, "#broken + '!'"));

        assertThat(table.get("broken")).hasSize(SIZE).containsOnly("N/A");
        // Зависимая колонка считается по значениям по умолчанию
        assertThat(table.get("dependent")).hasSize(SIZE).containsOnly("N/A!");
    }

    @Test
    @DisplayName("Result that doesn't fit the column is a generation error")
    void fail_calculate_with_invalid_result() {
        sendFailed(
                request(sequence("id"), expression("number", BaseType.INTEGER, "'abc'")),
                "result [abc] can't be converted to type [int]");
        sendFailed(
                request(sequence("id"), expression("flag", BaseType.BOOLEAN, "#id")),
                "result [1] can't be converted to type [bool]");
        sendFailed(
                request(sequence("id"), expression("ratio", BaseType.DOUBLE, "#id / 0.0")),
                "can't be converted to type [double]");
        sendFailed(
                request(sequence("id"), notNull(expression("required", BaseType.STRING, "null"))),
                "expression returned null, but field [notNull] is true");
        sendFailed(
                request(
                        dictionary("word", true, "abcdefghijklmnopqrstuvwxyz"),
                        expression("long", BaseType.STRING, "#word + #word + #word")),
                "result can't be longer than max = [60] characters");
        // Строка не умножается на число даже целым литералом
        sendFailed(
                request(dictionary("word", true, "12.5"), expression("product", BaseType.STRING, "#word * 2")),
                "can't calculate expression for row [1]");
    }

    @Test
    @DisplayName("Invalid references between columns - validation error")
    void fail_generate_with_invalid_references() {
        sendInvalid(
                request(sequence("id"), expression("total", BaseType.INTEGER, "#missing + 1")),
                "total",
                "expression references unknown column [missing]");
        sendInvalid(
                request(sequence("id"), expression("total", BaseType.INTEGER, "#total + #id")),
                "total",
                "expression references its own column [total]");
        sendInvalid(
                request(sequence("id"), sequence("id"), expression("total", BaseType.INTEGER, "#id + 1")),
                "total",
                "expression references column [id], but [2] columns have this name");

        GenerateTableRequest cycle = request(
                sequence("id"),
                expression("a", BaseType.INTEGER, "#b + #id"),
                expression("b", BaseType.INTEGER, "#c + 1"),
                expression("c", BaseType.INTEGER, "#a + 1"),
                expression("outside", BaseType.INTEGER, "#a + 1"));
        for (String column : List.of("a", "b", "c")) {
            sendInvalid(cycle, column, "expression is part of cyclic dependency [a -> b -> c -> a]");
        }
    }

    @Test
    @DisplayName("Invalid expression or column parameters - validation error")
    void fail_generate_with_invalid_expression() {
        sendInvalid(
                request(sequence("id"), expression("total", BaseType.INTEGER, "#id +")),
                "total",
                "field [expression] has invalid syntax");
        sendInvalid(
                request(expression("hack", BaseType.STRING, "T(java.lang.Runtime).getRuntime().exec('calc')")),
                "hack",
                "field [expression] contains unsupported construct");
        sendInvalid(
                request(sequence("id"), expression("total", BaseType.INTEGER, "id * 2")),
                "total",
                "A column is referenced with a leading #, for example #id");
        sendInvalid(
                request(sequence("id"), expression("total", BaseType.INTEGER, "#sqrt(#id)")),
                "total",
                "function [#sqrt] is not supported");
        sendInvalid(request(expression("blank", BaseType.STRING, " ")), "blank", "field [expression] can't be blank");
        sendInvalid(
                request(expression("absent", BaseType.STRING, null)), "absent", "field [expression] can't be blank");
        sendInvalid(
                request(expression("long", BaseType.STRING, "1 + ".repeat(300) + "1")),
                "long",
                "field [expression] can't be longer than max = [1000]");

        ColumnDto unique = expression("unique", BaseType.INTEGER, "#id");
        unique.getConfig().setUnique(true);
        sendInvalid(request(sequence("id"), unique), "unique", "field [unique] is not supported");

        ColumnDto customType = expression("custom", BaseType.STRING, "#id").setType(CustomType.UUID.getName());
        sendInvalid(request(sequence("id"), customType), "custom", "field [type] must be one of base types");

        ColumnDto unknownConverter = expression("converted", BaseType.INTEGER, "#id")
                .setConverters(List.of(new ConverterDto().setName("unknown")));
        sendInvalid(
                request(sequence("id"), unknownConverter),
                "converted",
                "converter with name [unknown] is not supported by app");

        ColumnDto boolWithConverter = expression("flag", BaseType.BOOLEAN, "#id > 1")
                .setConverters(List.of(new ConverterDto().setName(ConverterType.UPPER_CASE.getName())));
        sendInvalid(request(sequence("id"), boolWithConverter), "flag", "converters are not supported for type [bool]");
    }

    @Test
    @DisplayName("Numeric converters are applied to calculated numbers")
    void successful_calculate_with_numeric_converters() {
        ColumnDto doubled = expression("doubled", BaseType.INTEGER, "#id * 2")
                .setConverters(List.of(
                        new ConverterDto().setName(ConverterType.PLUS.getName()).setArguments(new Object[] {100})));

        Map<String, List<String>> table = generate(sequence("id"), doubled);

        for (int row = 0; row < SIZE; row++) {
            long id = Long.parseLong(table.get("id").get(row));
            assertThat(table.get("doubled").get(row)).isEqualTo(String.valueOf(id * 2 + 100));
        }
    }

    @Test
    @DisplayName("Enabled expression strategy is listed among strategies")
    void strategy_is_listed() {
        RestAssured.given()
                .when()
                .get("/v1/strategies")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("strategies.name", hasItem(GenerationStrategy.EXPRESSION.getName()));
    }

    private ColumnDto sequence(String name) {
        return new ColumnDto()
                .setName(name)
                .setType(BaseType.INTEGER.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.SHIFT.getType())
                        .setStart("1")
                        .setStep("1")
                        .setNotNull(true));
    }

    private ColumnDto constant(String name, Object value) {
        return new ColumnDto()
                .setName(name)
                .setType(BaseType.STRING.getValue())
                .setGenerationStrategy(GenerationStrategy.CONSTANT.getName())
                .setConfig(new ConfigDto().setConstant(value));
    }

    private ColumnDto dictionary(String name, boolean notNull, String... values) {
        return new ColumnDto()
                .setName(name)
                .setType(BaseType.STRING.getValue())
                .setGenerationStrategy(GenerationStrategy.DICTIONARY.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.getType())
                        .setNotNull(notNull)
                        .setDictionaries(Set.of(Set.of(values))));
    }

    private ColumnDto expression(String name, BaseType type, String expression) {
        return new ColumnDto()
                .setName(name)
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.EXPRESSION.getName())
                .setConfig(new ConfigDto().setExpression(expression));
    }

    private ColumnDto notNull(ColumnDto column) {
        column.getConfig().setNotNull(true);
        return column;
    }

    private GenerateTableRequest request(ColumnDto... columns) {
        return new GenerateTableRequest()
                .setSize(SIZE)
                .setFileType(FileType.CSV.name())
                .setColumns(List.of(columns));
    }

    private Map<String, List<String>> generate(ColumnDto... columns) {
        String csv = new String(sendGenerateRequest(request(columns)), StandardCharsets.UTF_8);
        CSVFormat format = CSVFormat.DEFAULT
                .builder()
                .setDelimiter(";")
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();

        Map<String, List<String>> table = new LinkedHashMap<>();
        try (CSVParser parser = CSVParser.parse(csv, format)) {
            parser.getHeaderNames().forEach(header -> table.put(header, new ArrayList<>()));
            for (CSVRecord record : parser) {
                table.forEach((header, values) -> values.add(record.get(header)));
            }
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }

        return table;
    }

    private void sendInvalid(GenerateTableRequest request, String columnName, String expectedMessage) {
        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("columnErrors.'" + columnName + "'", hasItem(containsString(expectedMessage)));
    }

    private void sendFailed(GenerateTableRequest request, String expectedMessage) {
        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body("message", containsString(expectedMessage));
    }
}
