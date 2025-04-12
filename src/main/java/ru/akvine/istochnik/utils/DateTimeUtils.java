package ru.akvine.istochnik.utils;

import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

@UtilityClass
public class DateTimeUtils {
    public final static DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public final static DateTimeFormatter DEFAULT_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    public final static DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public final static List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("yyyyMMdd"),
            DateTimeFormatter.ofPattern("dd MMM yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
    );

    public final static List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyyMMdd HHmmss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")
    );

    public LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null!");
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public boolean isDate(String input) {
        if (StringUtils.isBlank(input)) {
            return false;
        }

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDate.parse(input, formatter);
                return true;
            } catch (DateTimeParseException ignored) {}
        }
        return false;
    }

    public boolean isDateTime(String input) {
        if (StringUtils.isBlank(input)) {
            return false;
        }

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                LocalDateTime.parse(input, formatter);
                return true;
            } catch (DateTimeParseException ignored) {}
        }
        return false;
    }

    @Nullable
    public DateTimeFormatter extractFromLocalDate(String input) {
        if (StringUtils.isBlank(input)) {
            return null;
        }

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDate.parse(input, formatter);
                return formatter;
            } catch (DateTimeParseException ignored) {}
        }

        return null;
    }

    @Nullable
    public DateTimeFormatter extractFromLocalDateTime(String input) {
        if (StringUtils.isBlank(input)) {
            return null;
        }

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                LocalDateTime.parse(input, formatter);
                return formatter;
            } catch (DateTimeParseException ignored) {}
        }

        return null;
    }

    public LocalDateTime toLocalDateTime(String date) {
        return toLocalDateTime(date, DEFAULT_DATE_TIME_FORMATTER);
    }

    public LocalDateTime toLocalDateTime(String date, DateTimeFormatter formatter) {
        Asserts.isNotNull(date, "date can't be null");
        Asserts.isNotNull(formatter, "dateFormatter can't be null");
        return LocalDateTime.parse(date, formatter);
    }

    public LocalDate toLocalDate(String date) {
        return toLocalDate(date, DEFAULT_DATE_FORMATTER);
    }

    public LocalDate toLocalDate(String date, DateTimeFormatter formatter) {
        Asserts.isNotNull(date, "date can't be null");
        Asserts.isNotNull(formatter, "dateFormatter can't be null");
        return LocalDate.parse(date, formatter);
    }

    public LocalTime toLocalTime(String time) {
        return toLocalTime(time, DEFAULT_TIME_FORMATTER);
    }

    public LocalTime toLocalTime(String time, DateTimeFormatter formatter) {
        Asserts.isNotNull(time);
        Asserts.isNotNull(formatter);
        return LocalTime.parse(time, formatter);
    }

    public String formatLocalDateTime(LocalDateTime localDateTime) {
        return formatLocalDateTime(localDateTime, DEFAULT_DATE_TIME_FORMATTER);
    }

    public String formatLocalDateTime(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter) {
        if (localDateTime == null) {
            throw new IllegalArgumentException("localDateTime cannot be null!");
        }
        return localDateTime.format(dateTimeFormatter);
    }


}
