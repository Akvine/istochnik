package ru.akvine.istochnik.enums;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;

import java.util.List;

@AllArgsConstructor
@Getter
public enum BaseType {
    INTEGER("int", List.of(
            FilterType.ABS,
            FilterType.COS,
            FilterType.DIVIDE,
            FilterType.MINUS,
            FilterType.PLUS,
            FilterType.POW,
            FilterType.SIN
    )),
    DOUBLE("double", List.of(
            FilterType.ABS,
            FilterType.CEIL,
            FilterType.COS,
            FilterType.DIVIDE,
            FilterType.FLOOR,
            FilterType.MINUS,
            FilterType.PLUS,
            FilterType.POW,
            FilterType.ROUND,
            FilterType.SIN
    )),
    STRING("str", List.of(
            FilterType.BASE64,
            FilterType.LOWER_CASE,
            FilterType.REPLACE_ALL,
            FilterType.SUBSTRING,
            FilterType.TRIM,
            FilterType.UPPER_CASE,
            FilterType.DATE_FORMAT,
            FilterType.CAPITALIZE,
            FilterType.REMOVE_WHITESPACES,
            FilterType.RANDOM_NUMERIC_REPLACE,
            FilterType.RANDOM_RUSSIAN_REPLACE,
            FilterType.RANDOM_ENGLISH_REPLACE,
            FilterType.RANDOM_REPLACE
    )),
    BOOLEAN("bool", List.of());

    private final String value;
    private final List<FilterType> supportedFilterType;

    public static BaseType safeFrom(String type) {
        if (StringUtils.isBlank(type)) {
            throw new IllegalArgumentException("type is blank!");
        }

        for (BaseType baseType : values()) {
            if (baseType.getValue().equals(type)) {
                return baseType;
            }
        }

        throw new UnsupportedTypeGenerationException("Base type = [" + type + "] is not supported!");
    }

    public boolean isSupported(FilterType filterType) {
        return supportedFilterType.contains(filterType);
    }

    @Nullable
    public static BaseType from(String type) {
        try {
            return safeFrom(type);
        } catch (IllegalArgumentException | UnsupportedTypeGenerationException exception) {
            return null;
        }
    }
}
