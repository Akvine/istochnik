package ru.akvine.istochnik.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;

@Getter
@AllArgsConstructor
public enum FilterType {
    /**
     * Фильтры для чисел
     */
    ABS("abs","filter.abs.code"),
    CEIL("ceil", "filter.ceil.code"),
    COS("cos","filter.cos.code"),
    SIN("sin", "filter.sin.code"),
    DIVIDE("divide","filter.divide.code"),
    FLOOR("floor","filter.floor.code"),
    MINUS("minus","filter.minus.code"),
    PLUS("plus", "filter.plus.code"),
    POW("pow", "filter.pow.code"),
    ROUND("round", "filter.round.code"),
    KAIF("kaif", "filter.kaif.code"),

    /**
     * Фильтры для строк
     */
    BASE64("base64", "filter.base64.code"),
    LOWER_CASE("lowerCase", "filter.lowerCase.code"),
    REPLACE_ALL("replaceAll", "filter.replaceAll.code"),
    SUBSTRING("substring", "filter.substring.code"),
    TRIM("trim", "filter.trim.code"),
    UPPER_CASE("upperCase", "filter.upperCase.code");

    private final String name;
    private final String descriptionCode;

    public static FilterType safeFrom(String value) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("Filter type can't be blank!");
        }

        for (FilterType type : values()) {
            if (type.getName().equalsIgnoreCase(value)) {
                return type;
            }
        }

        throw new UnsupportedTypeGenerationException("Filter type = [" + value + "] is not supported by app!");
    }
}
