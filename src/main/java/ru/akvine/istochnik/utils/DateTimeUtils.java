package ru.akvine.istochnik.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@UtilityClass
public class DateTimeUtils {
    public final static DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null!");
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public LocalDateTime toLocalDateTime(String date) {
        return toLocalDateTime(date, DEFAULT_DATE_TIME_FORMATTER);
    }

    public LocalDateTime toLocalDateTime(String date, DateTimeFormatter formatter) {
        Asserts.isNotNull(date, "date can't be null");
        Asserts.isNotNull(formatter, "dateFormatter can't be null");
        return LocalDateTime.parse(date, formatter);
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
