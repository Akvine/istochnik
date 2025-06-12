package ru.akvine.istochnik.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;

@Getter
@AllArgsConstructor
public enum ConverterType {
    /**
     * Конвертеры для чисел
     */
    ABS("abs","converter.abs.code"),
    CEIL("ceil", "converter.ceil.code"),
    COS("cos","converter.cos.code"),
    SIN("sin", "converter.sin.code"),
    DIVIDE("divide","converter.divide.code"),
    FLOOR("floor","converter.floor.code"),
    MINUS("minus","converter.minus.code"),
    PLUS("plus", "converter.plus.code"),
    POW("pow", "converter.pow.code"),
    ROUND("round", "converter.round.code"),
    LOG("log", "converter.log.code"),
    LOG10("log10", "converter.log.code"),
    EXP("exp", "converter.exp.code"),
    MOD("mod", "converter.mod.code"),
    TAN("tan", "converter.tan.code"),
    COT("cot", "converter.cot.code"),
    ARCSIN("arcsin", "converter.arcsin.code"),
    ARCCOS("arccos", "converter.arccos.code"),
    ARCTAN("arctan", "converter.arctan.code"),
    ZSCORE("zscore", "converter.zscore.code"),
    MINMAXSCALING("minMaxScaling", "converter.minMaxScaling.code"),
    NEGATIVE("negative", "converter.negative.code"),


    /**
     * Конвертеры для строк
     */
    BASE64("base64", "converter.base64.code"),
    LOWER_CASE("lowerCase", "converter.lowerCase.code"),
    REPLACE_ALL("replaceAll", "converter.replaceAll.code"),
    SUBSTRING("substring", "converter.substring.code"),
    TRIM("trim", "converter.trim.code"),
    UPPER_CASE("upperCase", "converter.upperCase.code"),
    DATE_FORMAT("format", "converter.format.code"),
    CAPITALIZE("capitalize", "converter.capitalize.code"),
    REMOVE_WHITESPACES("removeWhitespaces", "converter.remove.whitespaces.code"),
    INVERSION("inversion", "converter.inversion.code"),
    RANDOM_NUMERIC_REPLACE("randomNumericReplace", "converter.random.numeric.replace.code"),
    RANDOM_RUSSIAN_REPLACE("randomRussianReplace", "converter.random.russian.replace.code"),
    RANDOM_ENGLISH_REPLACE("randomEnglishReplace", "converter.random.english.replace.code"),
    RANDOM_REPLACE("randomReplace", "converter.random.english.replace.code"),
    ADD_BEFORE("addBefore", "converter.add.before.code"),
    ADD_AFTER("addAfter", "converter.add.after.code"),
    REPEAT("repeat", "converter.repeat.code"),
    REVERSE("reverse", "converter.reverse.code"),
    TRANSLIT("translit", "converter.translit.code"),
    RANDOM_CASE("randomCase", "converter.random.case.code"),
    MD5("md5", "converter.md5.code"),
    SHA256("sha256", "converter.sha256.code");

    private final String name;
    private final String descriptionCode;

    public static ConverterType safeFrom(String value) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("Converter type can't be blank!");
        }

        for (ConverterType type : values()) {
            if (type.getName().equalsIgnoreCase(value)) {
                return type;
            }
        }

        throw new UnsupportedTypeGenerationException("Converter type = [" + value + "] is not supported by app!");
    }
}
