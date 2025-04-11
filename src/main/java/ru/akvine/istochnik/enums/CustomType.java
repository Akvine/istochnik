package ru.akvine.istochnik.enums;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;

@Getter
@AllArgsConstructor
public enum CustomType {
    UUID(BaseType.STRING, "uuid"),
    DATETIME(BaseType.STRING, "datetime"),
    TIME(BaseType.STRING, "time"),
    DATE(BaseType.STRING, "date"),
    SNILS(BaseType.INTEGER, "snils"),
    INN_ORG(BaseType.INTEGER, "inn_org"),
    INN_PERSONAL(BaseType.INTEGER, "inn_pers");

    private final BaseType baseType;
    private final String name;

    public static CustomType safeFrom(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Custom type = [" + name + "] can't be blank!");
        }

        for (CustomType customType : values()) {
            if (customType.getName().equals(name)) {
                return customType;
            }
        }

        throw new UnsupportedTypeGenerationException("Custom type = [" + name + "] is not supported by app!");
    }

    @Nullable
    public static CustomType from(String name) {
        try {
            return safeFrom(name);
        } catch (UnsupportedTypeGenerationException exception) {
            return null;
        }
    }
}
