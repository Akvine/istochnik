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

    /**
     * Фильтры для строк
     */
    BASE64("base64", "filter.base64.code"),
    LOWER_CASE("lowerCase", "filter.lowerCase.code"),
    REPLACE_ALL("replaceAll", "filter.replaceAll.code"),
    SUBSTRING("substring", "filter.substring.code"),
    TRIM("trim", "filter.trim.code"),
    UPPER_CASE("upperCase", "filter.upperCase.code"),
    DATE_FORMAT("format", "filter.format.code"),
    CAPITALIZE("capitalize", "filter.capitalize.code"),
    REMOVE_WHITESPACES("removeWhitespaces", "filter.remove.whitespaces.code"),
    INVERSION("inversion", "filter.inversion.code"),
    RANDOM_NUMERIC_REPLACE("randomNumericReplace", "filter.random.numeric.replace.code"),
    RANDOM_RUSSIAN_REPLACE("randomRussianReplace", "filter.random.russian.replace.code"),
    RANDOM_ENGLISH_REPLACE("randomEnglishReplace", "filter.random.english.replace.code"),
    RANDOM_REPLACE("randomReplace", "filter.random.english.replace.code"),
    ADD_BEFORE("addBefore", "filter.add.before.code"),
    ADD_AFTER("addAfter", "filter.add.after.code"),
    REPEAT("repeat", "filter.repeat.code"),
    REVERSE("reverse", "filter.reverse.code"),
    TRANSLIT("translit", "filter.translit.code"),
    RANDOM_CASE("randomCase", "filter.random.case.code");

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
