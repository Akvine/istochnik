package ru.akvine.istochnik.utils;

import jakarta.annotation.Nullable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ZonedDateTimeUtils {
    public final String FORMAT_EXAMPLES =
            "2025-12-25T10:30:00+03:00[Europe/Moscow] or 2025-12-25 10:30:00 Europe/Moscow";

    // Формат типа datetime, дополненный зоной или смещением: 2025-12-25 10:30:00 Europe/Moscow
    private final DateTimeFormatter DATE_TIME_WITH_ZONE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss VV");

    private final int ISO_DATE_LENGTH = 10;
    private final int MIN_ISO_ZONED_DATE_TIME_LENGTH = 20;

    /**
     * Разбирает дату и время с зоной в формате ISO-8601 (2025-12-25T10:30:00+03:00[Europe/Moscow])
     * или в формате "yyyy-MM-dd HH:mm:ss VV" (2025-12-25 10:30:00 Europe/Moscow)
     */
    public ZonedDateTime parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("zoned datetime can't be null");
        }

        String trimmed = value.trim();
        try {
            return ZonedDateTime.parse(trimmed);
        } catch (DateTimeParseException exception) {
            return ZonedDateTime.parse(trimmed, DATE_TIME_WITH_ZONE_FORMATTER);
        }
    }

    /**
     * Возвращает null, если значение не является датой и временем с зоной в формате ISO-8601
     */
    @Nullable
    public ZonedDateTime tryParseIso(String value) {
        // Дешёвая проверка до разбора, чтобы не платить исключением за каждое неподходящее значение
        if (value == null
                || value.length() < MIN_ISO_ZONED_DATE_TIME_LENGTH
                || value.charAt(ISO_DATE_LENGTH) != 'T') {
            return null;
        }

        try {
            return ZonedDateTime.parse(value);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    public String format(ZonedDateTime value) {
        return DateTimeFormatter.ISO_ZONED_DATE_TIME.format(value);
    }
}
