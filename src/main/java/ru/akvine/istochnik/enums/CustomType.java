package ru.akvine.istochnik.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;

@Getter
@AllArgsConstructor
public enum CustomType {
    UUID(BaseType.STRING, "uuid"),
    DATETIME(BaseType.STRING, "dateTime"),
    TIME(BaseType.STRING, "time");

    private final BaseType baseType;
    private final String name;

    public static CustomType safeFrom(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Custom type = [" + name + "] can't be blank!");
        }

        switch (name.toLowerCase()) {
            case "uuid" -> {
                return UUID;
            }
            case "datetime" -> {
                return DATETIME;
            }
            case "time" -> {
                return TIME;
            }
            default -> throw new UnsupportedTypeGenerationException("Custom type = [" + name + "] is not supported by app!");
        }
    }
}
