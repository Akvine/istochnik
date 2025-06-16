package ru.akvine.istochnik.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@AllArgsConstructor
public enum Topic {
    /**
     * Топики по теме "Адрес"
     */
    STREET_ADDRESS("topic.address.street_address.code"),
    CITY_NAME("topic.address.city_name.code"),
    COUNTRY("topic.address.country.code"),
    ZIP_CODE("topic.address.zip_code.code"),
    FULL_ADDRESS("topic.address.full_address.code"),

    /**
     * Топики по теме "Инициалы"
     */
    FIRST_NAME("topic.name.first_name.code"),
    LAST_NAME("topic.name.last_name.code"),
    FULL_NAME("topic.name.full_name.code"),
    MALE_FIRST_NAME("topic.name.male_first_name.code"),
    FEMALE_FIRST_NAME("topic.name.female_first_name.code"),

    /**
     * Топики по теме "Штрих-код"
     */
    EAN8("topic.barcode.ean8.code"),
    EAN13("topic.barcode.ean13.code"),
    GTIN8("topic.barcode.gtin8.code"),
    GTIN12("topic.barcode.gtin12.code"),

    /**
     * Топики по теме "Телефон"
     */
    PHONE_NUMBER("topic.phone.number.code"),
    CELL_PHONE("topic.phone.cell_number.code"),
    INTERNATIONAL_NUMBER("topic.phone.international_number.code"),

    /**
     * Топики по теме "Финансы"
     */
    IBAN("topic.finance.iban.code"),
    BIC("topic.finance.bic.code"),

    /**
     * Топики по теме "Интернет"
     */
    IPV4("topic.internet.ip_v4.code"),
    IPV6("topic.internet.ip_v6.code"),
    MAC("topic.internet.mac.code"),
    URL("topic.internet.url.code"),
    PASSWORD("topic.internet.password.code"),

    /**
     * Топики по теме "Автомобили"
     */
    VIN("topic.vehicle.vin.code"),
    VEHICLE_NUMBER("topic.vehicle.number.code"),

    /**
     * Топики по теме "Паспорт"
     */
    INTERNATIONAL_RUSSIAN_PASSPORT("topic.passport.international.russian.code"),
    INTERNATIONAL_US_PASSPORT("topic.passport.international.us.code"),
    INTERNATIONAL_COMMON_PASSPORT("topic.passport.international.common.code"),
    RUSSIAN_PASSPORT("topic.passport.russian.code"),
    US_PASSPORT("topic.passport.us.code");

    private final String code;

    public static Topic safeFrom(String value) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("Value is blank!");
        }

        for (Topic topic : values()) {
            if (topic.name().equalsIgnoreCase(value)) {
                return topic;
            }
        }

        throw new UnsupportedOperationException("Topic = [" + value + "] is not supported by app!");
    }
}
