package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.ConverterDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.ConverterType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;

@DisplayName("Multiply converter tests")
public class MultiplyConverterTest extends ApiBaseTest {

    @Test
    @DisplayName("Apply MULTIPLY converter to INTEGER - successfully")
    void successful_apply_multiply_at_int_values() {
        BaseType type = BaseType.INTEGER;

        List<?> result = convert(type, sendGenerateRequest(request(type, "1", "1", new Object[] {"3"})));

        assertThat(result).isEqualTo(List.of(3L, 6L, 9L, 12L));
    }

    @Test
    @DisplayName("Apply MULTIPLY converter to INTEGER with fractional argument - result is truncated")
    void successful_apply_multiply_with_fractional_argument_at_int_values() {
        BaseType type = BaseType.INTEGER;

        List<?> result = convert(type, sendGenerateRequest(request(type, "1", "1", new Object[] {2.5})));

        assertThat(result).isEqualTo(List.of(2L, 5L, 7L, 10L));
    }

    @Test
    @DisplayName("Apply MULTIPLY converter to DOUBLE - successfully")
    void successful_apply_multiply_at_double_values() {
        BaseType type = BaseType.DOUBLE;

        List<?> result = convert(type, sendGenerateRequest(request(type, "0", "0.5", new Object[] {2.5})));

        assertThat(result).isEqualTo(List.of(0.0, 1.25, 2.5, 3.75));
    }

    @Test
    @DisplayName("Apply MULTIPLY converter without argument - error")
    void multiply_without_argument() {
        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request(BaseType.INTEGER, "1", "1", new Object[] {}))
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(containsString("converter with name [multiply] has invalid arguments"));
    }

    private GenerateTableRequest request(BaseType type, String start, String step, Object[] arguments) {
        ColumnDto column = new ColumnDto()
                .setName("column")
                .setType(type.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.SHIFT.getType())
                        .setStart(start)
                        .setEnd("100")
                        .setStep(step))
                .setConverters(List.of(new ConverterDto()
                        .setName(ConverterType.MULTIPLY.getName())
                        .setArguments(arguments)));

        return new GenerateTableRequest()
                .setSize(4)
                .setFileType(FileType.CSV.name())
                .setColumns(List.of(column));
    }
}
