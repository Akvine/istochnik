package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;

@DisplayName("SQL dialect tests")
public class SqlDialectTest extends ApiBaseTest {

    @Test
    @DisplayName("Request with dialect - file is written in this dialect")
    void successful_generate_in_dialect() {
        assertThat(generate(request(FileType.SQL, "mssql")))
                .startsWith("INSERT INTO [dbo].[users] ([active]) VALUES\n")
                .doesNotContain("TRUE", "FALSE");
        assertThat(generate(request(FileType.SQL, "PostgreSQL")))
                .startsWith("INSERT INTO \"dbo\".\"users\" (\"active\")");
    }

    @Test
    @DisplayName("Request without dialect - file is the same as before dialects appeared")
    void successful_generate_without_dialect() {
        assertThat(generate(request(FileType.SQL, null))).startsWith("INSERT INTO dbo.users (ACTIVE) VALUES\n");
    }

    @Test
    @DisplayName("Unknown dialect or dialect for not SQL file - validation error")
    void fail_generate_with_invalid_dialect() {
        sendInvalid(request(FileType.SQL, "db2"), "SQL dialect = [db2] is not supported by app");
        sendInvalid(request(FileType.CSV, "postgresql"), "Field 'sqlDialect' can be used only with file type = SQL");
    }

    private String generate(GenerateTableRequest request) {
        return new String(sendGenerateRequest(request), StandardCharsets.UTF_8);
    }

    private void sendInvalid(GenerateTableRequest request, String expectedMessage) {
        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("generalInfoErrors", containsString(expectedMessage));
    }

    private GenerateTableRequest request(FileType fileType, String sqlDialect) {
        ColumnDto column = new ColumnDto()
                .setName("active")
                .setType(BaseType.BOOLEAN.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(
                        new ConfigDto().setRangeType(RangeType.RANDOM.getType()).setNotNull(true));

        return new GenerateTableRequest()
                .setSize(20)
                .setFileType(fileType.name())
                .setTableName("dbo.users")
                .setSqlDialect(sqlDialect)
                .setColumns(List.of(column));
    }
}
