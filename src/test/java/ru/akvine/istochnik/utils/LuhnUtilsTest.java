package ru.akvine.istochnik.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LuhnUtilsTest {

    @Test
    @DisplayName("Check digit is calculated for well-known card numbers and IMEI")
    void calculate_check_digit() {
        // Общеизвестные тестовые номера Visa и Mastercard, а также IMEI из описания алгоритма
        assertThat(LuhnUtils.calculateCheckDigit("411111111111111")).isEqualTo(1);
        assertThat(LuhnUtils.calculateCheckDigit("555555555555444")).isEqualTo(4);
        assertThat(LuhnUtils.calculateCheckDigit("49015420323751")).isEqualTo(8);
    }

    @Test
    @DisplayName("Number is valid only with correct check digit")
    void is_valid() {
        assertThat(LuhnUtils.isValid("4111111111111111")).isTrue();
        assertThat(LuhnUtils.isValid("5555555555554444")).isTrue();
        assertThat(LuhnUtils.isValid("490154203237518")).isTrue();

        assertThat(LuhnUtils.isValid("4111111111111112")).isFalse();
        // Перестановка соседних цифр
        assertThat(LuhnUtils.isValid("4111111111111171")).isFalse();
        assertThat(LuhnUtils.isValid("41111111111111a1")).isFalse();
        assertThat(LuhnUtils.isValid("")).isFalse();
        assertThat(LuhnUtils.isValid(null)).isFalse();
    }
}
