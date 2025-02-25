package ru.akvine.istochnik.enums;

import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;

@AllArgsConstructor
@Getter
public enum Type {
    INTEGER("integer"),
    LOCALDATETIME("localDateTime"),
    UUID("uuid"),
    DOUBLE("double");

    private final String value;

    public static Type from(String type) {
        if (StringUtils.isBlank(type)) {
            throw new IllegalArgumentException("type is blank!");
        }

        switch (type) {
            case "integer" -> {
                return INTEGER;
            }
            case "localDateTime" -> {
                return LOCALDATETIME;
            }
            case "uuid" -> {
                return UUID;
            }
            case "double" -> {
                return DOUBLE;
            }
            default -> throw new UnsupportedTypeGenerationException("Type = [" + type + "] is not supported by app!");
        }
    }
}
