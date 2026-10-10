package ru.akvine.istochnik.enums;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;

@Getter
@AllArgsConstructor
public enum CustomType {
    UUID(BaseType.STRING, "custom.type.uuid.code", "uuid", "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"),
    DATETIME(BaseType.STRING, "custom.type.datetime.code", "datetime", "2025-12-25 10:30:00"),
    TIME(BaseType.STRING, "custom.type.time.code", "time", "14:00:00"),
    DATE(BaseType.STRING, "custom.type.date.code", "date", "2025-12-25"),
    ZONED_DATETIME(
            BaseType.STRING,
            "custom.type.zoned.datetime.code",
            "zoned_datetime",
            "2025-12-25T10:30:00+03:00[Europe/Moscow]"),
    ZONE_ID(BaseType.STRING, "custom.type.zone.id.code", "zone_id", "Europe/Moscow"),
    DECIMAL(BaseType.STRING, "custom.type.decimal.code", "decimal", "1234.56"),
    SNILS(BaseType.INTEGER, "custom.type.snils.code", "snils", "12345678901"),
    INN_ORG(BaseType.INTEGER, "custom.type.inn.org.code", "inn_org", "7701206411"),
    INN_PERSONAL(BaseType.INTEGER, "custom.type.inn.pers.code", "inn_pers", "270903791924"),
    OGRN(BaseType.INTEGER, "custom.type.ogrn.code", "ogrn", "10277000673288"),
    IMEI(BaseType.STRING, "custom.type.imei.code", "imei", "865434044201239"),
    CARD_MIR(BaseType.STRING, "custom.type.card.mir.code", "card_mir", "2200123456789019"),
    CARD_VISA(BaseType.STRING, "custom.type.card.visa.code", "card_visa", "4111111111111111"),
    CARD_MASTERCARD(BaseType.STRING, "custom.type.card.mastercard.code", "card_mastercard", "5555555555554444");

    private final BaseType baseType;
    private final String code;
    private final String name;
    private final String example;

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
