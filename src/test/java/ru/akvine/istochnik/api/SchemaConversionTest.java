package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import ru.akvine.istochnik.api.common.configs.RestMethods;

// Очередь пула в общих настройках тестов вмещает одну задачу, а таблицы из схем содержат много колонок
@TestPropertySource(
        properties = {
            "parallel.executor.threads-count=4",
            "parallel.executor.queue-capacity=50",
            "schema.conversion.max-file-size-kb=4"
        })
@DisplayName("Schema conversion tests")
public class SchemaConversionTest extends ApiBaseTest {
    private static final int MAX_FILE_SIZE_BYTES = 4 * 1024;
    private static final String CSV_SEPARATOR = ";";
    private static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}";

    private static final String SQL_SCHEMA =
            """
            -- V1__init.sql
            CREATE TYPE order_status AS ENUM ('NEW', 'PAID', 'CANCELLED');

            CREATE TABLE users (
                id          BIGSERIAL PRIMARY KEY,
                email       VARCHAR(255) NOT NULL UNIQUE,
                full_name   VARCHAR(100) NOT NULL,
                age         SMALLINT,
                balance     NUMERIC(12, 2) NOT NULL DEFAULT 0,
                rating      DOUBLE PRECISION NOT NULL,
                is_active   BOOLEAN NOT NULL DEFAULT true,
                external_id UUID NOT NULL,
                birth_date  DATE NOT NULL,
                wake_up_at  TIME NOT NULL,
                created_at  TIMESTAMP NOT NULL DEFAULT now(),
                updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
                settings    JSONB NOT NULL DEFAULT '{}',
                tags        TEXT[],
                location    GEOMETRY
            );

            CREATE TABLE orders (
                id      BIGINT GENERATED ALWAYS AS IDENTITY,
                user_id BIGINT NOT NULL REFERENCES users (id),
                status  order_status NOT NULL,
                total   NUMERIC(10, 2) NOT NULL
            );
            """;

    private static final String XML_SCHEMA =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog">
                <changeSet id="1" author="akvine">
                    <createTable tableName="products">
                        <column name="id" type="BIGINT" autoIncrement="true">
                            <constraints primaryKey="true" nullable="false"/>
                        </column>
                        <column name="title" type="VARCHAR(8)">
                            <constraints nullable="false"/>
                        </column>
                        <column name="price" type="DECIMAL(10, 2)">
                            <constraints nullable="false"/>
                        </column>
                        <column name="category_id" type="INT">
                            <constraints nullable="false" foreignKeyName="fk_category" references="categories(id)"/>
                        </column>
                        <column name="created_at" type="DATETIME"/>
                    </createTable>
                </changeSet>
            </databaseChangeLog>
            """;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("SQL script is converted to generation requests")
    void successful_convert_sql() {
        convert(schemaFile("V1__init.sql", SQL_SCHEMA).queryParam("size", 20).queryParam("fileType", "csv"))
                .statusCode(HttpStatus.OK.value())
                .body("status", equalTo("SUCCESS"))
                .body("count", equalTo(2))
                .body("requests.tableName", contains("users", "orders"))
                .body("requests[0].size", equalTo(20))
                .body("requests[0].fileType", equalTo("csv"))
                .body(
                        "requests[0].columns.name",
                        contains(
                                "id",
                                "email",
                                "full_name",
                                "age",
                                "balance",
                                "rating",
                                "is_active",
                                "external_id",
                                "birth_date",
                                "wake_up_at",
                                "created_at",
                                "updated_at",
                                "settings",
                                "tags",
                                "location"))
                .body(
                        "requests[0].columns.type",
                        contains(
                                "int",
                                "str",
                                "str",
                                "int",
                                "decimal",
                                "double",
                                "bool",
                                "uuid",
                                "date",
                                "time",
                                "datetime",
                                "zoned_datetime",
                                "str",
                                "str",
                                "str"))
                .body("requests[0].columns[0].config.rangeType", equalTo("shift"))
                .body("requests[0].columns[1].config.unique", equalTo(true))
                .body("requests[0].columns[1].config.notNull", equalTo(true))
                .body("requests[0].columns[3].config.end", equalTo("32767"))
                .body("requests[0].columns[4].config.start", equalTo("0.00"))
                .body("requests[0].columns[8].config.start", matchesPattern(DATE_PATTERN))
                .body("requests[0].columns[12].generationStrategy", equalTo("constant"))
                .body("requests[0].columns[12].config.constant", equalTo("{}"))
                // Колонку, которую всегда заполняет база данных, генерировать нельзя
                .body("requests[1].columns.name", contains("user_id", "status", "total"))
                .body("requests[1].columns[0].config.end", equalTo("21"))
                .body("requests[1].columns[1].generationStrategy", equalTo("dictionary"))
                .body("requests[1].columns[1].config.dictionaries[0]", contains("NEW", "PAID", "CANCELLED"))
                .body("warnings", hasItem(containsString("column [location]: type [geometry] is not supported")))
                .body("warnings", hasItem(containsString("column [id]: value is always generated by database")))
                .body("warnings", hasItem(containsString("column [user_id]: references [users(id)]")));
    }

    @Test
    @DisplayName("Liquibase XML change log is converted to generation requests")
    void successful_convert_xml() {
        convert(schemaFile("changelog.xml", XML_SCHEMA))
                .statusCode(HttpStatus.OK.value())
                .body("count", equalTo(1))
                .body("requests[0].tableName", equalTo("products"))
                // Значения по умолчанию: 100 строк в формате SQL
                .body("requests[0].size", equalTo(100))
                .body("requests[0].fileType", equalTo("sql"))
                .body("requests[0].columns.name", contains("id", "title", "price", "category_id", "created_at"))
                .body("requests[0].columns.type", contains("int", "str", "decimal", "int", "datetime"))
                .body("requests[0].columns[0].config.rangeType", equalTo("shift"))
                .body("requests[0].columns[1].config.length", equalTo(8))
                .body("requests[0].columns[3].config.end", equalTo("101"))
                .body("warnings.size()", equalTo(1));
    }

    @Test
    @DisplayName("Byte order mark at the beginning of the file is ignored")
    void successful_convert_file_with_byte_order_mark() {
        convert(schemaFile("changelog.xml", "\uFEFF" + XML_SCHEMA))
                .statusCode(HttpStatus.OK.value())
                .body("requests.tableName", contains("products"));
        convert(schemaFile("V1__init.sql", "\uFEFF" + SQL_SCHEMA))
                .statusCode(HttpStatus.OK.value())
                .body("requests.tableName", contains("users", "orders"));
    }

    @Test
    @DisplayName("Fields with default values are not printed in generation requests")
    void successful_convert_prints_only_changed_fields() {
        convert(schemaFile("changelog.xml", XML_SCHEMA))
                .statusCode(HttpStatus.OK.value())
                .body(
                        "requests[0].columns[0].keySet()",
                        containsInAnyOrder("name", "type", "generationStrategy", "config"))
                .body(
                        "requests[0].columns[0].config.keySet()",
                        containsInAnyOrder("notNull", "rangeType", "start", "step"))
                .body("requests[0].columns[4].config", not(hasKey("notNull")));
    }

    @Test
    @DisplayName("Every generation request built from schema is accepted by generator")
    void successful_generate_by_converted_schema() throws Exception {
        int size = 20;
        for (String schemaFileName : List.of("V1__init.sql", "changelog.xml")) {
            String schema = schemaFileName.endsWith(".sql") ? SQL_SCHEMA : XML_SCHEMA;
            String response = convert(schemaFile(schemaFileName, schema)
                            .queryParam("size", size)
                            .queryParam("fileType", "csv"))
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .asString();

            for (JsonNode request : objectMapper.readTree(response).get("requests")) {
                List<String> lines = generate(request.toString()).lines().toList();

                String[] header = lines.get(0).split(CSV_SEPARATOR);
                assertThat(header).hasSize(request.get("columns").size());
                assertThat(lines).hasSize(size + 1);
            }
        }
    }

    @Test
    @DisplayName("Generated values match column constraints of the schema")
    void successful_generate_values_by_schema_constraints() throws Exception {
        int size = 30;
        String response = convert(schemaFile("V1__init.sql", SQL_SCHEMA)
                        .queryParam("size", size)
                        .queryParam("fileType", "csv"))
                .extract()
                .asString();
        JsonNode users = objectMapper.readTree(response).get("requests").get(0);

        List<String[]> rows = generate(users.toString())
                .lines()
                .skip(1)
                // Пустые значения в конце строки не должны теряться при разбиении
                .map(line -> line.split(CSV_SEPARATOR, -1))
                .toList();

        // id - последовательность с единицы
        assertThat(rows.stream().map(row -> row[0]).toList())
                .isEqualTo(
                        IntStream.rangeClosed(1, size).mapToObj(String::valueOf).toList());
        // email - NOT NULL UNIQUE
        List<String> emails = rows.stream().map(row -> row[1]).toList();
        assertThat(emails).doesNotContain("").hasSize(size);
        assertThat(new HashSet<>(emails)).hasSize(size);
        // balance - NUMERIC(12, 2) NOT NULL
        assertThat(rows.stream().map(row -> row[4]).toList()).allMatch(value -> value.matches("\\d+\\.\\d{2}"));
        // age - единственная колонка без NOT NULL среди первых пяти
        assertThat(rows.stream().map(row -> row[3]).toList())
                .allMatch(value -> value.isEmpty() || Integer.parseInt(value) < 32767);
        assertThat(rows.stream().map(row -> row[8]).toList()).allMatch(value -> value.matches(DATE_PATTERN));
        assertThat(rows.stream().map(row -> row[12]).toList()).containsOnly("{}");
    }

    @Test
    @DisplayName("Generation request built from schema gives SQL inserts into table of the schema")
    void successful_generate_sql_by_converted_schema() throws Exception {
        String response = convert(schemaFile("changelog.xml", XML_SCHEMA).queryParam("size", 3))
                .extract()
                .asString();
        JsonNode products = objectMapper.readTree(response).get("requests").get(0);

        String sql = generate(products.toString());

        assertThat(sql).startsWith("INSERT INTO products (ID, TITLE, PRICE, CATEGORY_ID, CREATED_AT)");
    }

    @Test
    @DisplayName("Schema type is taken from parameter or from file extension")
    void schema_type_resolving() {
        convert(schemaFile("schema.txt", SQL_SCHEMA).queryParam("type", "SQL"))
                .statusCode(HttpStatus.OK.value())
                .body("count", equalTo(2));

        // Параметр важнее расширения: журнал Liquibase как SQL-скрипт таблиц не содержит
        expectError(
                schemaFile("changelog.xml", XML_SCHEMA).queryParam("type", "sql"),
                "Schema doesn't contain table definitions");
        expectError(
                schemaFile("schema.txt", SQL_SCHEMA),
                "Can't determine schema type by file name = [schema.txt]. Set parameter [type]");
        expectError(
                schemaFile("schema.sql", SQL_SCHEMA).queryParam("type", "yaml"),
                "Schema type = [yaml] is not supported by app");
    }

    @Test
    @DisplayName("Invalid schema or parameters - conversion error")
    void fail_convert() {
        expectError(
                RestAssured.given()
                        .multiPart("other", "x.sql", "CREATE TABLE t (id int);".getBytes(StandardCharsets.UTF_8)),
                "Schema file can't be empty");
        expectError(schemaFile("empty.sql", ""), "Schema file can't be empty");
        expectError(
                schemaFile("no_tables.sql", "SELECT 1; DROP TABLE users;"), "Schema doesn't contain table definitions");
        expectError(schemaFile("broken.xml", "<databaseChangeLog><changeSet>"), "Schema is not a valid XML");
        expectError(
                schemaFile("only_generated.sql", "CREATE TABLE t (id bigint GENERATED ALWAYS AS IDENTITY);"),
                "Schema doesn't contain tables with columns to generate");
        expectError(
                schemaFile("schema.sql", SQL_SCHEMA).queryParam("fileType", "pdf"),
                "Target file type = [pdf] is not supported by app");
        expectError(schemaFile("schema.sql", SQL_SCHEMA).queryParam("size", -1), "size can't be less than 0");
        expectError(
                schemaFile("schema.sql", SQL_SCHEMA).queryParam("size", 10001),
                "Can't generate rows size = [10001] more than max = [10000]");

        String largeSchema = SQL_SCHEMA + "-- " + "x".repeat(MAX_FILE_SIZE_BYTES);
        expectError(schemaFile("large.sql", largeSchema), "is greater than max = [" + MAX_FILE_SIZE_BYTES + "] bytes");
    }

    @Test
    @DisplayName("File larger than multipart limit of Spring Boot is rejected")
    void fail_convert_file_larger_than_multipart_limit() {
        // По умолчанию Spring Boot принимает файлы не больше 1 МБ
        String hugeSchema = SQL_SCHEMA + "-- " + "x".repeat(1024 * 1024);

        convert(schemaFile("huge.sql", hugeSchema)).statusCode(HttpStatus.PAYLOAD_TOO_LARGE.value());
    }

    @Test
    @DisplayName("XML with DOCTYPE is rejected and content of server files is not disclosed")
    void fail_convert_xml_with_external_entity() {
        String xml =
                """
                <?xml version="1.0"?>
                <!DOCTYPE databaseChangeLog [<!ENTITY secret SYSTEM "file:///etc/hostname">]>
                <databaseChangeLog>
                    <changeSet id="1" author="a">
                        <createTable tableName="&secret;"><column name="id" type="INT"/></createTable>
                    </changeSet>
                </databaseChangeLog>
                """;

        expectError(schemaFile("changelog.xml", xml), "Schema is not a valid XML");
    }

    @Test
    @DisplayName("Statements other than table definitions don't produce requests or warnings")
    void successful_convert_ignores_other_statements() {
        String schema =
                """
                SET search_path TO public;
                CREATE SEQUENCE users_seq;
                CREATE TABLE users (id bigint PRIMARY KEY, name text NOT NULL);
                COMMENT ON TABLE users IS 'Пользователи; все';
                INSERT INTO users (id, name) VALUES (1, 'CREATE TABLE fake (id int);');
                GRANT SELECT ON users TO reader;
                """;

        convert(schemaFile("V2__users.sql", schema))
                .statusCode(HttpStatus.OK.value())
                .body("requests.tableName", contains("users"))
                .body("requests[0].columns.name", contains("id", "name"))
                .body("warnings", empty());
    }

    private RequestSpecification schemaFile(String fileName, String content) {
        return RestAssured.given().multiPart("file", fileName, content.getBytes(StandardCharsets.UTF_8));
    }

    private ValidatableResponse convert(RequestSpecification request) {
        return request.accept(ContentType.JSON)
                .when()
                .post(RestMethods.CONVERT_SCHEMA_ENDPOINT)
                .then();
    }

    private void expectError(RequestSpecification request, String expectedMessage) {
        convert(request)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("status", equalTo("FAIL"))
                .body("code", equalTo("schema.conversion.error"))
                .body("message", containsString(expectedMessage));
    }

    private String generate(String request) {
        byte[] file = RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asByteArray();
        return new String(file, StandardCharsets.UTF_8);
    }
}
