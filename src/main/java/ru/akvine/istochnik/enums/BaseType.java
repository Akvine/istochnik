package ru.akvine.istochnik.enums;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;

@AllArgsConstructor
@Getter
public enum BaseType {
    INTEGER("integer"),
    STRING("string"),
    DOUBLE("double"),
    BOOLEAN("boolean");

    private final String value;

    public static BaseType safeFrom(String type) {
        if (StringUtils.isBlank(type)) {
            throw new IllegalArgumentException("type is blank!");
        }

        switch (type) {
            case "integer" -> {
                return INTEGER;
            }
            case "double" -> {
                return DOUBLE;
            }
            case "string" -> {
                return STRING;
            }
            case "boolean" -> {
                return BOOLEAN;
            }
            default -> throw new UnsupportedTypeGenerationException("Type = [" + type + "] is not supported by app!");
        }
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
