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
import ru.akvine.istochnik.api.ApiBaseTest;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.utils.LuhnUtils;
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
    @DisplayName("Generate OKVED values")
    void successful_generate_okved_values() {
        List<String> result = generate(CustomType.OKVED, new ConfigDto().setNotNull(true));

        assertThat(result).hasSize(SIZE);
        assertThat(result).allSatisfy(value -> {
            assertThat(value).matches("\\d{2}\\.[1-9]\\d(\\.[1-9]{1,2})?");
            assertThat(number(value, 0, 2)).isBetween(1, 99);
        });
        // Встречаются коды всех трёх уровней детализации
        assertThat(result.stream().map(String::length).distinct()).containsExactlyInAnyOrder(5, 7, 8);
    }

    @Test
    @DisplayName("Generate OKTMO values")
    void successful_generate_oktmo_values() {
        List<String> result = generate(CustomType.OKTMO, new ConfigDto().setNotNull(true));

        assertThat(result).hasSize(SIZE);
        assertThat(result).allSatisfy(value -> {
            assertThat(value).matches("\\d{8}");
            assertThat(number(value, 0, 2)).isBetween(1, 99);
            assertThat(value.substring(2)).matches("7[0-4]\\d000|6[0-5]\\d(000|1\\d{2}|4\\d{2})");
        });
    }

    @Test
    @DisplayName("Generate OKATO values")
    void successful_generate_okato_values() {
        List<String> result = generate(CustomType.OKATO, new ConfigDto().setNotNull(true));

        assertThat(result).hasSize(SIZE);
        assertThat(result).allSatisfy(value -> {
            assertThat(value).matches("\\d{11}");
            assertThat(number(value, 0, 2)).isBetween(1, 99);
            assertThat(value.substring(2, 6)).matches("2\\d{2}[58]|4\\d{2}[35]");
            assertThat(number(value, 8, 11)).isBetween(1, 199);
        });
    }

    @Test
    @DisplayName("Generate KBK values")
    void successful_generate_kbk_values() {
        List<String> result = generate(CustomType.KBK, new ConfigDto().setNotNull(true));

        assertThat(result).hasSize(SIZE);
        assertThat(result).allSatisfy(value -> {
            assertThat(value).matches("\\d{3}1\\d{16}");
            assertThat(number(value, 4, 6)).isBetween(1, 17);
            assertThat(number(value, 6, 8)).isBetween(1, 12);
            assertThat(number(value, 11, 13)).isBetween(1, 14);
            assertThat(value.substring(13, 17)).isIn("1000", "2100", "2200", "3000", "4000", "5000");
            assertThat(value.substring(17)).matches("1[1-8]0");
        });
    }

    @Test
    @DisplayName("Generate valid OMS policy values")
    void successful_generate_valid_oms_values() {
        List<String> result =
                generate(CustomType.OMS, new ConfigDto().setNotNull(true).setValid(true));

        assertThat(result).hasSize(SIZE);
        assertThat(result).allSatisfy(value -> {
            assertThat(value).matches("[1-9]\\d{15}");
            assertThat(LuhnUtils.isValid(value)).isTrue();
        });
    }

    @Test
    @DisplayName("Generate STS and PTS values")
    void successful_generate_vehicle_document_values() {
        List<String> sts = generate(CustomType.STS, new ConfigDto().setNotNull(true));
        List<String> pts = generate(CustomType.PTS, new ConfigDto().setNotNull(true));

        assertThat(sts).hasSize(SIZE);
        assertThat(sts).allSatisfy(value -> {
            assertThat(value).matches("\\d{2}(\\d{2}|[АВЕКМНОРСТУХ]{2})\\d{6}");
            assertThat(number(value, 0, 2)).isBetween(1, 99);
        });
        // У СТС встречаются и цифровые, и буквенные серии
        assertThat(sts).anySatisfy(value -> assertThat(value).matches("\\d{10}"));
        assertThat(sts).anySatisfy(value -> assertThat(value).doesNotMatch("\\d{10}"));

        assertThat(pts).hasSize(SIZE);
        assertThat(pts).allSatisfy(value -> {
            assertThat(value).matches("\\d{2}[АВЕКМНОРСТУХ]{2}\\d{6}");
            assertThat(number(value, 0, 2)).isBetween(1, 99);
        });
    }

    @Test
    @DisplayName("Examples of custom types match generated formats")
    void custom_type_examples_are_valid() {
        assertThat(LuhnUtils.isValid(CustomType.OMS.getExample())).isTrue();
        assertThat(CustomType.KBK.getExample()).matches("\\d{20}");
        assertThat(CustomType.OKTMO.getExample()).matches("\\d{8}");
        assertThat(CustomType.OKATO.getExample()).matches("\\d{11}");
    }

    @Test
    @DisplayName("Generate unique nullable requisites")
    void successful_generate_unique_nullable_values() {
        for (CustomType type : List.of(
                CustomType.KPP,
                CustomType.OGRNIP,
                CustomType.OKPO,
                CustomType.BIK,
                CustomType.OKVED,
                CustomType.OKTMO,
                CustomType.OKATO,
                CustomType.KBK,
                CustomType.OMS,
                CustomType.STS,
                CustomType.PTS)) {
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
                .anySatisfy(
                        value -> assertThat(RequisiteUtils.isValidOgrnip(value)).isFalse());
        assertThat(generate(CustomType.OKPO, new ConfigDto().setNotNull(true)))
                .anySatisfy(
                        value -> assertThat(RequisiteUtils.isValidOkpo(value)).isFalse());
        assertThat(generate(CustomType.OMS, new ConfigDto().setNotNull(true)))
                .anySatisfy(value -> assertThat(LuhnUtils.isValid(value)).isFalse());
        // Без флага valid БИК для счёта не нужен
        assertThat(generate(CustomType.BANK_ACCOUNT, new ConfigDto().setNotNull(true)))
                .allSatisfy(value -> assertThat(value).matches("\\d{20}"))
                .anySatisfy(value -> assertThat(RequisiteUtils.isValidBankAccount(BIK, value))
                        .isFalse());
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
