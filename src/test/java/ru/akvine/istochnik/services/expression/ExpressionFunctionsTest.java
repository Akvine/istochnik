package ru.akvine.istochnik.services.expression;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Expression functions tests")
public class ExpressionFunctionsTest {

    @Test
    @DisplayName("String functions")
    void string_functions() {
        assertThat(ExpressionFunctions.lower("Иван Petrov")).isEqualTo("иван petrov");
        assertThat(ExpressionFunctions.upper("Иван Petrov")).isEqualTo("ИВАН PETROV");
        assertThat(ExpressionFunctions.length("Иван")).isEqualTo(4L);

        assertThat(ExpressionFunctions.substring("Иванов", 0, 1)).isEqualTo("И");
        assertThat(ExpressionFunctions.substring("Иванов", 2, 4)).isEqualTo("ан");
        // Позиции за границами строки приводятся к границам
        assertThat(ExpressionFunctions.substring("Иванов", -3, 100)).isEqualTo("Иванов");
        assertThat(ExpressionFunctions.substring("Иванов", 4, 2)).isEmpty();
        assertThat(ExpressionFunctions.substring("Иванов", 10, 20)).isEmpty();
    }

    @Test
    @DisplayName("Number functions keep the type of the argument")
    void number_functions() {
        assertThat(ExpressionFunctions.abs(-5L)).isEqualTo(5L);
        assertThat(ExpressionFunctions.abs(-2.5)).isEqualTo(2.5);
        assertThat(ExpressionFunctions.abs(new BigDecimal("-2.50"))).isEqualTo(new BigDecimal("2.50"));

        assertThat(ExpressionFunctions.round(2.345, 2)).isEqualTo(2.35);
        assertThat(ExpressionFunctions.round(2.5, 0)).isEqualTo(3.0);
        assertThat(ExpressionFunctions.round(new BigDecimal("11.255"), 2)).isEqualTo(new BigDecimal("11.26"));
        assertThat(ExpressionFunctions.round(1250L, -2)).isEqualTo(1300L);
        assertThat(ExpressionFunctions.round(7L, 2)).isEqualTo(7L);

        assertThat(ExpressionFunctions.min(3L, 2.5)).isEqualTo(2.5);
        assertThat(ExpressionFunctions.max(3L, 2.5)).isEqualTo(3L);
        assertThat(ExpressionFunctions.max(new BigDecimal("10.10"), 10L)).isEqualTo(new BigDecimal("10.10"));

        assertThat(ExpressionFunctions.pow(2L, 10L)).isEqualTo(1024.0);
        assertThatThrownBy(() -> ExpressionFunctions.pow(10L, 1000L)).isInstanceOf(ArithmeticException.class);
    }

    @Test
    @DisplayName("Round scale is limited")
    void round_scale_is_limited() {
        assertThatThrownBy(() -> ExpressionFunctions.round(new BigDecimal("1.5"), 999_999_999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("scale");
    }

    @Test
    @DisplayName("Conversion to integer rounds half up and accepts numeric strings")
    void to_int() {
        assertThat(ExpressionFunctions.toInt(7L)).isEqualTo(7L);
        assertThat(ExpressionFunctions.toInt(2.5)).isEqualTo(3L);
        assertThat(ExpressionFunctions.toInt(-2.5)).isEqualTo(-3L);
        // 0.57 * 100 в двоичной арифметике равно 56.99999999999999
        assertThat(ExpressionFunctions.toInt(0.57 * 100)).isEqualTo(57L);
        assertThat(ExpressionFunctions.toInt(new BigDecimal("12.49"))).isEqualTo(12L);
        assertThat(ExpressionFunctions.toInt(" 42 ")).isEqualTo(42L);
        assertThat(ExpressionFunctions.toInt("1e3")).isEqualTo(1000L);

        assertThatThrownBy(() -> ExpressionFunctions.toInt("abc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("is not a number");
        assertThatThrownBy(() -> ExpressionFunctions.toInt(true)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ExpressionFunctions.toInt(1e30))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("out of integer range");
    }

    @Test
    @DisplayName("Conversion to double and string")
    void to_double_and_to_str() {
        assertThat(ExpressionFunctions.toDouble(3L)).isEqualTo(3.0);
        assertThat(ExpressionFunctions.toDouble("12.50")).isEqualTo(12.5);
        assertThat(ExpressionFunctions.toDouble(new BigDecimal("0.1"))).isEqualTo(0.1);
        assertThatThrownBy(() -> ExpressionFunctions.toDouble(Double.NaN)).isInstanceOf(ArithmeticException.class);

        assertThat(ExpressionFunctions.toStr(5L)).isEqualTo("5");
        assertThat(ExpressionFunctions.toStr(37.5)).isEqualTo("37.5");
        assertThat(ExpressionFunctions.toStr(new BigDecimal("37.50"))).isEqualTo("37.50");
        assertThat(ExpressionFunctions.toStr(true)).isEqualTo("true");
        assertThat(ExpressionFunctions.toStr("text")).isEqualTo("text");
        // Без экспоненты
        assertThat(ExpressionFunctions.toStr(1.0E10)).isEqualTo("10000000000");
        assertThat(ExpressionFunctions.toStr(new BigDecimal("1E+3"))).isEqualTo("1000");
    }

    @Test
    @DisplayName("Numbers with a huge exponent are not parsed")
    void huge_numbers_are_rejected() {
        assertThat(ExpressionFunctions.parseDecimal("1e999999999")).isNull();
        assertThat(ExpressionFunctions.parseDecimal("1e-999999999")).isNull();
        assertThat(ExpressionFunctions.parseDecimal("9".repeat(200))).isNull();
        assertThat(ExpressionFunctions.parseDecimal("12.50")).isEqualTo(new BigDecimal("12.50"));

        assertThatThrownBy(() -> ExpressionFunctions.toInt("1e999999999")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Date functions keep the format of the argument")
    void date_functions() {
        assertThat(ExpressionFunctions.plusDays("2025-12-25", 10L)).isEqualTo("2026-01-04");
        assertThat(ExpressionFunctions.plusDays("2025-12-25", -25L)).isEqualTo("2025-11-30");
        assertThat(ExpressionFunctions.plusMonths("2025-01-31", 1L)).isEqualTo("2025-02-28");
        assertThat(ExpressionFunctions.plusYears("2024-02-29", 1L)).isEqualTo("2025-02-28");
        assertThat(ExpressionFunctions.plusDays("2025-12-25T10:30:15", 1L)).isEqualTo("2025-12-26T10:30:15");

        assertThat(ExpressionFunctions.daysBetween("2025-01-01", "2025-01-31")).isEqualTo(30L);
        assertThat(ExpressionFunctions.daysBetween("2025-01-31", "2025-01-01")).isEqualTo(-30L);
        assertThat(ExpressionFunctions.daysBetween("2025-01-01T23:59:59", "2025-01-02"))
                .isEqualTo(1L);

        assertThatThrownBy(() -> ExpressionFunctions.plusDays("25.12.2025", 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("yyyy-MM-dd");
        assertThatThrownBy(() -> ExpressionFunctions.plusYears("2025-12-25", Long.MAX_VALUE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("out of range");
    }

    @Test
    @DisplayName("Functions return null for null arguments")
    void null_arguments() {
        assertThat(ExpressionFunctions.lower(null)).isNull();
        assertThat(ExpressionFunctions.upper(null)).isNull();
        assertThat(ExpressionFunctions.length(null)).isNull();
        assertThat(ExpressionFunctions.substring(null, 0, 1)).isNull();
        assertThat(ExpressionFunctions.substring("text", null, 1)).isNull();
        assertThat(ExpressionFunctions.abs(null)).isNull();
        assertThat(ExpressionFunctions.round(null, 2)).isNull();
        assertThat(ExpressionFunctions.round(2.5, null)).isNull();
        assertThat(ExpressionFunctions.min(null, 1L)).isNull();
        assertThat(ExpressionFunctions.max(1L, null)).isNull();
        assertThat(ExpressionFunctions.pow(null, 2L)).isNull();
        assertThat(ExpressionFunctions.toInt(null)).isNull();
        assertThat(ExpressionFunctions.toDouble(null)).isNull();
        assertThat(ExpressionFunctions.toStr(null)).isNull();
        assertThat(ExpressionFunctions.plusDays(null, 1L)).isNull();
        assertThat(ExpressionFunctions.plusDays("2025-12-25", null)).isNull();
        assertThat(ExpressionFunctions.daysBetween(null, "2025-12-25")).isNull();
    }
}
