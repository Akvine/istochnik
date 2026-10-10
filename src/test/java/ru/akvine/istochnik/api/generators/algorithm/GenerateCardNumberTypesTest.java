package ru.akvine.istochnik.api.generators.algorithm;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.api.ApiBaseTest;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.utils.LuhnUtils;

@DisplayName("Card number types tests")
public class GenerateCardNumberTypesTest extends ApiBaseTest {
    private static final int SIZE = 200;

    @Test
    @DisplayName("Generate valid Mir card numbers")
    void successful_generate_valid_mir_card_numbers() {
        List<String> result = generate(
                CustomType.CARD_MIR, new ConfigDto().setNotNull(true).setValid(true));

        assertValidCardNumbers(result);
        assertThat(result).allSatisfy(value -> assertThat(prefix(value, 4)).isBetween(2200, 2204));
    }

    @Test
    @DisplayName("Generate valid Visa card numbers")
    void successful_generate_valid_visa_card_numbers() {
        List<String> result = generate(
                CustomType.CARD_VISA, new ConfigDto().setNotNull(true).setValid(true));

        assertValidCardNumbers(result);
        assertThat(result).allSatisfy(value -> assertThat(value).startsWith("4"));
    }

    @Test
    @DisplayName("Generate valid Mastercard card numbers - both prefix ranges are used")
    void successful_generate_valid_mastercard_card_numbers() {
        List<String> result = generate(
                CustomType.CARD_MASTERCARD, new ConfigDto().setNotNull(true).setValid(true));

        assertValidCardNumbers(result);
        assertThat(result).allSatisfy(value -> assertThat(isMastercardPrefix(value)).isTrue());
        assertThat(result).anySatisfy(value -> assertThat(value).startsWith("5"));
        assertThat(result).anySatisfy(value -> assertThat(value).startsWith("2"));
    }

    @Test
    @DisplayName("Generate unique card numbers")
    void successful_generate_unique_card_numbers() {
        List<String> result = generate(
                CustomType.CARD_MIR,
                new ConfigDto().setNotNull(true).setValid(true).setUnique(true));

        assertThat(new HashSet<>(result)).hasSize(SIZE);
    }

    @Test
    @DisplayName("Generate card numbers without valid flag - check digit is not guaranteed")
    void successful_generate_card_numbers_without_valid_flag() {
        List<String> result = generate(CustomType.CARD_VISA, new ConfigDto().setNotNull(true));

        assertThat(result).allSatisfy(value -> assertThat(value).matches("4\\d{15}"));
        assertThat(result).anySatisfy(value -> assertThat(LuhnUtils.isValid(value)).isFalse());
    }

    @Test
    @DisplayName("Generate nullable card numbers - not null values are valid")
    void successful_generate_nullable_card_numbers() {
        List<String> result = generate(CustomType.CARD_MASTERCARD, new ConfigDto().setValid(true));

        // Пустая строка в CSV соответствует null
        assertThat(result).contains("");
        assertValidCardNumbers(result.stream().filter(value -> !value.isEmpty()).toList());
    }

    private void assertValidCardNumbers(List<String> cardNumbers) {
        assertThat(cardNumbers).isNotEmpty();
        assertThat(cardNumbers).allSatisfy(value -> {
            assertThat(value).matches("\\d{16}");
            assertThat(LuhnUtils.isValid(value)).isTrue();
        });
    }

    private boolean isMastercardPrefix(String cardNumber) {
        int twoDigits = prefix(cardNumber, 2);
        int fourDigits = prefix(cardNumber, 4);
        return (twoDigits >= 51 && twoDigits <= 55) || (fourDigits >= 2221 && fourDigits <= 2720);
    }

    private int prefix(String cardNumber, int length) {
        return Integer.parseInt(cardNumber.substring(0, length));
    }

    private List<String> generate(CustomType type, ConfigDto config) {
        ColumnDto column = new ColumnDto()
                .setName("column")
                .setType(type.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(config);

        GenerateTableRequest request = new GenerateTableRequest()
                .setSize(SIZE)
                .setFileType(FileType.CSV.name())
                .setColumns(List.of(column));

        return convert(BaseType.STRING, sendGenerateRequest(request)).stream()
                .map(String.class::cast)
                .toList();
    }
}
