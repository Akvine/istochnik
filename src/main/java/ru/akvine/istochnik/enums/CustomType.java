package ru.akvine.istochnik.enums;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;

@Getter
@AllArgsConstructor
public enum CustomType {
    UUID(BaseType.STRING, "custom.type.uuid.code","uuid"),
    DATETIME(BaseType.STRING, "custom.type.datetime.code","datetime"),
    TIME(BaseType.STRING, "custom.type.time.code","time"),
    DATE(BaseType.STRING, "custom.type.date.code","date"),
    SNILS(BaseType.INTEGER, "custom.type.snils.code","snils"),
    INN_ORG(BaseType.INTEGER, "custom.type.inn.org.code","inn_org"),
    INN_PERSONAL(BaseType.INTEGER, "custom.type.inn.pers.code","inn_pers"),
    OGRN(BaseType.INTEGER, "custom.type.ogrn.code","ogrn");

    private final BaseType baseType;
    private final String code;
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
