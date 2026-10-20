package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.SqlDialect;

// Очередь пула в общих настройках тестов вмещает одну задачу, а в примерах десятки колонок
@TestPropertySource(properties = {"parallel.executor.threads-count=4", "parallel.executor.queue-capacity=100"})
@DisplayName("Samples tests: every request from the samples folder works")
public class SamplesTest extends ApiBaseTest {
    private static final Path SAMPLES = Path.of("samples");
    private static final Path SCHEMAS = SAMPLES.resolve("schemas");

    private static final String JSON_EXTENSION = ".json";
    private static final String CSV_SEPARATOR = ";";
    // Файл XLSX - это zip-архив, он начинается с этой сигнатуры
    private static final String ZIP_SIGNATURE = "PK";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ParameterizedTest(name = "{0}")
    @MethodSource("generationSamples")
    @DisplayName("Generation request sample gives a file with all columns and rows")
    void successful_generate_by_sample(Path sample) throws IOException {
        String request = Files.readString(sample);

        byte[] file = generate(request);

        verifyFile(objectMapper.readTree(request), file);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("schemaSamples")
    @DisplayName("Schema sample is converted to generation requests accepted by generator")
    void successful_convert_schema_sample(Path sample) throws IOException {
        String response = RestAssured.given()
                .multiPart("file", sample.getFileName().toString(), Files.readAllBytes(sample))
                .queryParam("size", 15)
                .queryParam("fileType", "csv")
                .accept(ContentType.JSON)
                .when()
                .post(RestMethods.CONVERT_SCHEMA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asString();

        JsonNode requests = objectMapper.readTree(response).get("requests");
        assertThat(requests).isNotEmpty();
        for (JsonNode request : requests) {
            verifyFile(request, generate(request.toString()));
        }
    }

    static Stream<Path> generationSamples() throws IOException {
        return samples(SAMPLES).filter(sample -> !sample.startsWith(SCHEMAS));
    }

    static Stream<Path> schemaSamples() throws IOException {
        return samples(SCHEMAS);
    }

    private static Stream<Path> samples(Path directory) throws IOException {
        boolean requests = !directory.equals(SCHEMAS);
        try (Stream<Path> files = Files.walk(directory)) {
            List<Path> samples = files.filter(Files::isRegularFile)
                    .filter(file -> !requests || file.toString().endsWith(JSON_EXTENSION))
                    .sorted()
                    .toList();
            // Пустой список означал бы, что папка с примерами не найдена и тест ничего не проверяет
            assertThat(samples).isNotEmpty();
            return new ArrayList<>(samples).stream();
        }
    }

    private byte[] generate(String request) {
        return RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asByteArray();
    }

    private void verifyFile(JsonNode request, byte[] file) {
        int size = request.get("size").asInt();
        List<String> columnNames = new ArrayList<>();
        request.get("columns")
                .forEach(column -> columnNames.add(column.get("name").asText()));

        String text = new String(file, StandardCharsets.UTF_8);
        switch (FileType.from(request.get("fileType").asText())) {
            case CSV -> {
                List<String> lines = text.lines().toList();
                assertThat(lines).hasSize(size + 1);
                assertThat(lines.get(0)).isEqualTo(String.join(CSV_SEPARATOR, columnNames));
            }
            case SQL -> {
                String tableName = request.get("tableName").asText();
                String columns = String.join(", ", columnNames).toUpperCase();
                // В диалекте имена заключаются в кавычки этого диалекта
                SqlDialect dialect = request.has("sqlDialect")
                        ? SqlDialect.safeFrom(request.get("sqlDialect").asText())
                        : null;
                if (dialect != null) {
                    tableName = dialect.quoteTableName(tableName);
                    columns = String.join(
                            ", ", columnNames.stream().map(dialect::quoteName).toList());
                }
                assertThat(text).startsWith("INSERT INTO " + tableName + " (" + columns + ") VALUES");
                // Каждая строка таблицы - отдельная строка файла, начинающаяся со скобки
                assertThat(text.lines().filter(line -> line.startsWith("("))).hasSize(size);
            }
            case XLSX -> assertThat(text).startsWith(ZIP_SIGNATURE);
        }
    }
}
