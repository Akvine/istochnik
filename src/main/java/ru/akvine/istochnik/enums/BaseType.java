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
    INTEGER("int", "base.type.integer.code", List.of(
            ConverterType.ABS,
            ConverterType.DIVIDE,
            ConverterType.MINUS,
            ConverterType.PLUS,
            ConverterType.POW,
            ConverterType.MOD,
            ConverterType.NEGATIVE
    )),
    DOUBLE("double", "base.type.double.code", List.of(
            ConverterType.ABS,
            ConverterType.CEIL,
            ConverterType.COS,
            ConverterType.DIVIDE,
            ConverterType.FLOOR,
            ConverterType.MINUS,
            ConverterType.PLUS,
            ConverterType.POW,
            ConverterType.ROUND,
            ConverterType.SIN,
            ConverterType.TAN,
            ConverterType.LOG,
            ConverterType.LOG10,
            ConverterType.EXP,
            ConverterType.COT,
            ConverterType.ARCSIN,
            ConverterType.ARCCOS,
            ConverterType.ARCTAN,
            ConverterType.ZSCORE,
            ConverterType.MINMAXSCALING,
            ConverterType.NEGATIVE

    )),
    STRING("str", "base.type.string.code", List.of(
            ConverterType.BASE64,
            ConverterType.LOWER_CASE,
            ConverterType.REPLACE_ALL,
            ConverterType.SUBSTRING,
            ConverterType.TRIM,
            ConverterType.UPPER_CASE,
            ConverterType.DATE_FORMAT,
            ConverterType.CAPITALIZE,
            ConverterType.REMOVE_WHITESPACES,
            ConverterType.RANDOM_NUMERIC_REPLACE,
            ConverterType.RANDOM_RUSSIAN_REPLACE,
            ConverterType.RANDOM_ENGLISH_REPLACE,
            ConverterType.RANDOM_REPLACE,
            ConverterType.ADD_AFTER,
            ConverterType.ADD_BEFORE,
            ConverterType.REPEAT,
            ConverterType.REVERSE,
            ConverterType.TRANSLIT
    )),
    BOOLEAN("bool", "base.type.boolean.code", List.of());

    private final String value;
    private final String code;
    private final List<ConverterType> supportedConverterType;

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

    public boolean isSupported(ConverterType converterType) {
        return supportedConverterType.contains(converterType);
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
