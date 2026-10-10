package ru.akvine.istochnik.services.expression;

import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import lombok.experimental.UtilityClass;

/**
 * Функции, доступные в выражениях вычисляемых колонок: #lower(#name), #round(#price * #qty, 2).
 * Каждый открытый метод класса становится функцией с тем же именем, поэтому имена методов не должны повторяться.
 * Если любой из аргументов равен null, функция возвращает null
 */
@UtilityClass
public class ExpressionFunctions {
    // Числа с большим количеством цифр или порядком не поддерживаются: арифметика над ними слишком дорогая
    private static final int MAX_NUMBER_DIGITS = 100;
    private static final int MAX_ROUND_SCALE = 18;
    private static final int DATE_LENGTH = 10;

    public String lower(String value) {
        return value == null ? null : value.toLowerCase();
    }

    public String upper(String value) {
        return value == null ? null : value.toUpperCase();
    }

    public Long length(String value) {
        return value == null ? null : (long) value.length();
    }

    /**
     * Подстрока с позиции begin (включительно) до позиции end (не включая). Позиции считаются с нуля,
     * выход за границы строки ошибкой не считается
     */
    public String substring(String value, Integer begin, Integer end) {
        if (value == null || begin == null || end == null) {
            return null;
        }

        int from = Math.clamp(begin, 0, value.length());
        int to = Math.clamp(end, from, value.length());
        return value.substring(from, to);
    }

    public Number abs(Number value) {
        if (value == null) {
            return null;
        }

        if (isInteger(value)) {
            return Math.absExact(value.longValue());
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.abs();
        }
        return Math.abs(value.doubleValue());
    }

    /**
     * Округление до scale знаков после запятой. Тип числа сохраняется
     */
    public Number round(Number value, Integer scale) {
        if (value == null || scale == null) {
            return null;
        }
        if (Math.abs(scale) > MAX_ROUND_SCALE) {
            throw new IllegalArgumentException(
                    "scale = [" + scale + "] must be between -" + MAX_ROUND_SCALE + " and " + MAX_ROUND_SCALE);
        }

        BigDecimal rounded = toBigDecimal(value).setScale(scale, RoundingMode.HALF_UP);
        if (isInteger(value)) {
            return rounded.longValueExact();
        }
        if (value instanceof BigDecimal) {
            return rounded;
        }
        return rounded.doubleValue();
    }

    public Number min(Number first, Number second) {
        if (first == null || second == null) {
            return null;
        }

        return toBigDecimal(first).compareTo(toBigDecimal(second)) <= 0 ? first : second;
    }

    public Number max(Number first, Number second) {
        if (first == null || second == null) {
            return null;
        }

        return toBigDecimal(first).compareTo(toBigDecimal(second)) >= 0 ? first : second;
    }

    public Double pow(Number base, Number exponent) {
        if (base == null || exponent == null) {
            return null;
        }

        return requireFinite(Math.pow(base.doubleValue(), exponent.doubleValue()));
    }

    /**
     * Целое число из числа или строки с числом. Дробная часть округляется до ближайшего целого
     */
    public Long toInt(Object value) {
        if (value == null) {
            return null;
        }

        try {
            return toBigDecimal(value).setScale(0, RoundingMode.HALF_UP).longValueExact();
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException("value [" + value + "] is out of integer range");
        }
    }

    public Double toDouble(Object value) {
        if (value == null) {
            return null;
        }

        return requireFinite(toBigDecimal(value).doubleValue());
    }

    /**
     * Строковое представление значения. Числа записываются без экспоненты
     */
    public String toStr(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number number && !isInteger(number)) {
            return toBigDecimal(number).toPlainString();
        }
        return String.valueOf(value);
    }

    public String plusDays(String date, Number days) {
        return plus(date, days, ChronoUnit.DAYS);
    }

    public String plusMonths(String date, Number months) {
        return plus(date, months, ChronoUnit.MONTHS);
    }

    public String plusYears(String date, Number years) {
        return plus(date, years, ChronoUnit.YEARS);
    }

    /**
     * Количество полных дней от даты from до даты to. Отрицательное, если to раньше from
     */
    public Long daysBetween(String from, String to) {
        if (from == null || to == null) {
            return null;
        }

        return ChronoUnit.DAYS.between(
                parseDateTime(from).toLocalDate(), parseDateTime(to).toLocalDate());
    }

    /**
     * Разбирает строку с десятичным числом
     *
     * @return null, если строка не является числом или число не поддерживается
     */
    @Nullable
    static BigDecimal parseDecimal(String value) {
        if (value.length() > MAX_NUMBER_DIGITS) {
            return null;
        }

        try {
            BigDecimal decimal = new BigDecimal(value.trim());
            // Отсекает и запись с огромным порядком вроде 1e999999999
            return Math.abs(decimal.scale()) > MAX_NUMBER_DIGITS ? null : decimal;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        if (value instanceof Number number) {
            return isInteger(number)
                    ? BigDecimal.valueOf(number.longValue())
                    : BigDecimal.valueOf(requireFinite(number.doubleValue()));
        }
        if (value instanceof String text) {
            BigDecimal decimal = parseDecimal(text);
            if (decimal != null) {
                return decimal;
            }
        }

        throw new IllegalArgumentException("value [" + value + "] is not a number");
    }

    private boolean isInteger(Number value) {
        return value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte;
    }

    private double requireFinite(double value) {
        if (!Double.isFinite(value)) {
            throw new ArithmeticException("result is not a finite number");
        }

        return value;
    }

    // Дата остаётся датой, дата и время - датой и временем: формат результата совпадает с форматом аргумента
    private String plus(String value, Number amount, ChronoUnit unit) {
        if (value == null || amount == null) {
            return null;
        }

        LocalDateTime dateTime = parseDateTime(value);
        try {
            LocalDateTime shifted = dateTime.plus(amount.longValue(), unit);
            return isDate(value) ? shifted.toLocalDate().toString() : shifted.toString();
        } catch (DateTimeException | ArithmeticException exception) {
            throw new IllegalArgumentException("date [" + value + "] shifted by [" + amount + "] is out of range");
        }
    }

    private LocalDateTime parseDateTime(String value) {
        String text = value.trim();
        try {
            return isDate(text) ? LocalDate.parse(text).atStartOfDay() : LocalDateTime.parse(text);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("value [" + value
                    + "] is not a date in format yyyy-MM-dd or a date with time in format yyyy-MM-ddTHH:mm:ss");
        }
    }

    private boolean isDate(String value) {
        return value.trim().length() <= DATE_LENGTH;
    }
}
