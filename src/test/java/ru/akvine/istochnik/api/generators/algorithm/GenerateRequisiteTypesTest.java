package ru.akvine.istochnik.api.generators.algorithm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.api.ApiBaseTest;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.utils.RequisiteUtils;

@DisplayName("Requisite types tests")
public class GenerateRequisiteTypesTest extends ApiBaseTest {
    private static final int SIZE = 200;
    private static final String BIK = "044525225";

    @Test
    @DisplayName("Generate KPP values")
    void successful_generate_kpp_values() {
        List<String> result = generate(CustomType.KPP, new ConfigDto().setNotNull(true));

        assertThat(result).hasSize(SIZE);
        assertThat(result).allSatisfy(value -> {
            assertThat(value).matches("\\d{9}");
            assertThat(number(value, 0, 2)).isBetween(1, 99);
            assertThat(number(value, 2, 4)).isBetween(1, 99);
            assertThat(number(value, 6, 9)).isBetween(1, 999);
        });
    }

    @Test
    @DisplayName("Generate valid OGRNIP values")
    void successful_generate_valid_ogrnip_values() {
        List<String> result =
                generate(CustomType.OGRNIP, new ConfigDto().setNotNull(true).setValid(true));

        assertThat(result).hasSize(SIZE);
        assertThat(result).allSatisfy(value -> {
            assertThat(value).matches("3\\d{14}");
            assertThat(number(value, 1, 3)).isBetween(4, 25);
            assertThat(number(value, 3, 5)).isBetween(1, 99);
            assertThat(RequisiteUtils.isValidOgrnip(value)).isTrue();
        });
    }

    @Test
    @DisplayName("Generate valid OKPO values")
    void successful_generate_valid_okpo_values() {
        List<String> result =
                generate(CustomType.OKPO, new ConfigDto().setNotNull(true).setValid(true));

        assertThat(result).hasSize(SIZE);
        assertThat(result).allSatisfy(value -> {
            assertThat(value).matches("\\d{8}");
            assertThat(RequisiteUtils.isValidOkpo(value)).isTrue();
        });
    }

    @Test
    @DisplayName("Generate BIK values")
    void successful_generate_bik_values() {
        List<String> result = generate(CustomType.BIK, new ConfigDto().setNotNull(true));

        assertThat(result).hasSize(SIZE);
        assertThat(result).allSatisfy(value -> {
            assertThat(value).matches("04\\d{7}");
            assertThat(number(value, 2, 4)).isBetween(1, 99);
            assertThat(number(value, 6, 9)).isBetween(50, 999);
        });
    }

    @Test
    @DisplayName("Generate valid bank accounts - check key matches BIK from start")
    void successful_generate_valid_bank_account_values() {
        List<String> result = generate(
                CustomType.BANK_ACCOUNT,
                new ConfigDto().setNotNull(true).setValid(true).setStart(BIK));

        assertThat(result).hasSize(SIZE);
        assertThat(result).allSatisfy(value -> {
            assertThat(value).matches("40[78]0[123]810\\d{12}");
            assertThat(RequisiteUtils.isValidBankAccount(BIK, value)).isTrue();
        });
    }

    @Test
    @DisplayName("Generate unique nullable requisites")
    void successful_generate_unique_nullable_values() {
        for (CustomType type : List.of(CustomType.KPP, CustomType.OGRNIP, CustomType.OKPO, CustomType.BIK)) {
            List<String> result = generate(type, new ConfigDto().setUnique(true).setValid(true));

            assertThat(result).hasSize(SIZE);
            assertThat(new HashSet<>(result)).hasSize(SIZE);
            // Пустая строка в CSV соответствует null
            assertThat(result).contains("");
        }
    }

    @Test
    @DisplayName("Generate requisites without valid flag - check digit is not guaranteed")
    void successful_generate_values_without_valid_flag() {
        assertThat(generate(CustomType.OGRNIP, new ConfigDto().setNotNull(true)))
                .anySatisfy(value -> assertThat(RequisiteUtils.isValidOgrnip(value)).isFalse());
        assertThat(generate(CustomType.OKPO, new ConfigDto().setNotNull(true)))
                .anySatisfy(value -> assertThat(RequisiteUtils.isValidOkpo(value)).isFalse());
        // Без флага valid БИК для счёта не нужен
        assertThat(generate(CustomType.BANK_ACCOUNT, new ConfigDto().setNotNull(true)))
                .allSatisfy(value -> assertThat(value).matches("\\d{20}"))
                .anySatisfy(value -> assertThat(RequisiteUtils.isValidBankAccount(BIK, value)).isFalse());
    }

    @Test
    @DisplayName("Valid bank account without BIK or with invalid BIK - validation error")
    void fail_generate_bank_account_with_invalid_bik() {
        sendInvalid(new ConfigDto().setNotNull(true).setValid(true), "field [start] must contain BIK");
        sendInvalid(new ConfigDto().setNotNull(true).setValid(true).setStart("0445252"), "field [start] is not a BIK");
        sendInvalid(new ConfigDto().setNotNull(true).setStart("04452522A"), "field [start] is not a BIK");
    }

    private void sendInvalid(ConfigDto config, String expectedMessage) {
        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request(CustomType.BANK_ACCOUNT, config))
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(containsString(expectedMessage));
    }

    private int number(String value, int beginIndex, int endIndex) {
        return Integer.parseInt(value.substring(beginIndex, endIndex));
    }

    private List<String> generate(CustomType type, ConfigDto config) {
        return convert(BaseType.STRING, sendGenerateRequest(request(type, config))).stream()
                .map(String.class::cast)
                .toList();
    }

    private GenerateTableRequest request(CustomType type, ConfigDto config) {
        ColumnDto column = new ColumnDto()
                .setName("column")
                .setType(type.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(config);

        return new GenerateTableRequest()
                .setSize(SIZE)
                .setFileType(FileType.CSV.name())
                .setColumns(List.of(column));
    }
}
