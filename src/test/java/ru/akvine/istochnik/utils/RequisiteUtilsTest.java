package ru.akvine.istochnik.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RequisiteUtilsTest {

    @Test
    @DisplayName("OGRNIP check digit")
    void ogrnip_check_digit() {
        assertThat(RequisiteUtils.calculateOgrnipCheckDigit("30450011600015")).isEqualTo(7);

        assertThat(RequisiteUtils.isValidOgrnip("304500116000157")).isTrue();
        assertThat(RequisiteUtils.isValidOgrnip("304500116000158")).isFalse();
        assertThat(RequisiteUtils.isValidOgrnip("30450011600015")).isFalse();
        assertThat(RequisiteUtils.isValidOgrnip(null)).isFalse();
    }

    @Test
    @DisplayName("OKPO check digit for real organizations")
    void okpo_check_digit() {
        // ОКПО Сбербанка и Газпрома
        assertThat(RequisiteUtils.calculateOkpoCheckDigit("0003253")).isEqualTo(7);
        assertThat(RequisiteUtils.calculateOkpoCheckDigit("0004077")).isEqualTo(8);

        assertThat(RequisiteUtils.isValidOkpo("00032537")).isTrue();
        assertThat(RequisiteUtils.isValidOkpo("00040778")).isTrue();
        assertThat(RequisiteUtils.isValidOkpo("00032538")).isFalse();
        assertThat(RequisiteUtils.isValidOkpo("0003253a")).isFalse();
    }

    @Test
    @DisplayName("OKPO check digit when the first pass gives 10 - weights are shifted")
    void okpo_check_digit_with_second_pass() {
        // 5 * 2 = 10 при весах от 1, поэтому расчёт повторяется с весами от 3: 5 * 4 = 20, остаток 9
        assertThat(RequisiteUtils.calculateOkpoCheckDigit("0500000")).isEqualTo(9);
        // 9 * 6 = 54, остаток 10; с весами от 3: 9 * 8 = 72, остаток 6
        assertThat(RequisiteUtils.calculateOkpoCheckDigit("0000090")).isEqualTo(6);
    }

    @Test
    @DisplayName("Bank account key for accounts in Bank of Russia divisions")
    void bank_account_key_for_bank_of_russia_division() {
        // Корреспондентские счета Сбербанка и Т-Банка в ГУ Банка России по ЦФО
        String bik = "044525000";

        assertThat(RequisiteUtils.calculateBankAccountKey(bik, "30101810400000000225"))
                .isEqualTo(4);
        assertThat(RequisiteUtils.isValidBankAccount(bik, "30101810400000000225"))
                .isTrue();
        assertThat(RequisiteUtils.isValidBankAccount(bik, "30101810145250000974"))
                .isTrue();
        assertThat(RequisiteUtils.isValidBankAccount(bik, "30101810545250000974"))
                .isFalse();
    }

    @Test
    @DisplayName("Bank account key for accounts in credit organizations depends on BIK")
    void bank_account_key_for_credit_organization() {
        assertThat(RequisiteUtils.calculateBankAccountKey("044525225", "40702810038000012345"))
                .isEqualTo(9);
        assertThat(RequisiteUtils.isValidBankAccount("044525225", "40702810938000012345"))
                .isTrue();
        // Тот же счёт не проходит проверку с БИК другого банка
        assertThat(RequisiteUtils.isValidBankAccount("044525974", "40702810938000012345"))
                .isFalse();
        assertThat(RequisiteUtils.isValidBankAccount("04452522", "40702810938000012345"))
                .isFalse();
    }
}
