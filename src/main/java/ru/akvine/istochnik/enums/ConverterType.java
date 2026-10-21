package ru.akvine.istochnik.enums;

import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;

@Getter
@AllArgsConstructor
public enum ConverterType {
    /**
     * Конвертеры для чисел. Конвертеры, доступные и для int, и для double, в примере получают целые числа:
     * для обоих типов результат одинаковый
     */
    ABS("abs", "converter.abs.code", Example.of(List.of(-7, 3), List.of(7, 3))),
    CEIL("ceil", "converter.ceil.code", Example.of(List.of(2.1, -2.1), List.of(3.0, -2.0))),
    COS("cos", "converter.cos.code", Example.of(List.of(0.0, 3.141592653589793), List.of(1.0, -1.0))),
    SIN("sin", "converter.sin.code", Example.of(List.of(0.0, 1.5707963267948966), List.of(0.0, 1.0))),
    DIVIDE("divide", "converter.divide.code", Example.of(List.of(10, 6), List.of(5, 3), 2)),
    MULTIPLY("multiply", "converter.multiply.code", Example.of(List.of(4, -2), List.of(12, -6), 3)),
    FLOOR("floor", "converter.floor.code", Example.of(List.of(2.9, -2.1), List.of(2.0, -3.0))),
    MINUS("minus", "converter.minus.code", Example.of(List.of(12, 3), List.of(7, -2), 5)),
    PLUS("plus", "converter.plus.code", Example.of(List.of(12, -3), List.of(17, 2), 5)),
    POW("pow", "converter.pow.code", Example.of(List.of(3, -4), List.of(9, 16), 2)),
    ROUND("round", "converter.round.code", Example.of(List.of(3.14159, 2.71828), List.of(3.14, 2.72), 2)),
    // Аргумент логарифмов обязателен, но в вычислении не участвует
    LOG("log", "converter.log.code", Example.of(List.of(1.0, 2.718281828459045), List.of(0.0, 1.0), 1)),
    LOG10("log10", "converter.log10.code", Example.of(List.of(1.0, 1000.0), List.of(0.0, 3.0), 1)),
    EXP("exp", "converter.exp.code", Example.of(List.of(0.0, 1.0), List.of(1.0, 2.718281828459045))),
    MOD("mod", "converter.mod.code", Example.of(List.of(10, 12), List.of(1, 0), 3)),
    TAN("tan", "converter.tan.code", Example.of(List.of(0.0, 1.0), List.of(0.0, 1.5574077246549023))),
    COT("cot", "converter.cot.code", Example.of(List.of(1.0), List.of(0.6420926159343306))),
    ARCSIN("arcsin", "converter.arcsin.code", Example.of(List.of(0.0, 1.0), List.of(0.0, 1.5707963267948966))),
    ARCCOS("arccos", "converter.arccos.code", Example.of(List.of(1.0, -1.0), List.of(0.0, 3.141592653589793))),
    ARCTAN("arctan", "converter.arctan.code", Example.of(List.of(0.0, 1.0), List.of(0.0, 0.7853981633974483))),
    ZSCORE(
            "zscore",
            "converter.zscore.code",
            Example.of(
                    List.of(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0),
                    List.of(-1.5, -0.5, -0.5, -0.5, 0.0, 0.0, 1.0, 2.0))),
    MINMAXSCALING(
            "minMaxScaling",
            "converter.minMaxScaling.code",
            Example.of(List.of(10.0, 15.0, 20.0, 30.0), List.of(0.0, 0.25, 0.5, 1.0))),
    NEGATIVE("negative", "converter.negative.code", Example.of(List.of(5, -3), List.of(-5, 3))),
    FACTORIAL("factorial", "converter.factorial.code", Example.of(List.of(3, 5), List.of(6, 120))),

    /**
     * Конвертеры для строк
     */
    BASE64("base64", "converter.base64.code", Example.of(List.of("hello"), List.of("aGVsbG8="))),
    LOWER_CASE("lowerCase", "converter.lowerCase.code", Example.of(List.of("Hello World"), List.of("hello world"))),
    REPLACE_ALL(
            "replaceAll",
            "converter.replaceAll.code",
            Example.of(List.of("+7 (912) 345-67-89"), List.of("79123456789"), "[^0-9]", "")),
    SUBSTRING("substring", "converter.substring.code", Example.of(List.of("2025-12-25"), List.of("2025"), 0, 4)),
    TRIM("trim", "converter.trim.code", Example.of(List.of("  text  "), List.of("text"))),
    UPPER_CASE("upperCase", "converter.upperCase.code", Example.of(List.of("Hello World"), List.of("HELLO WORLD"))),
    DATE_FORMAT(
            "format", "converter.format.code", Example.of(List.of("2025-12-25"), List.of("25.12.2025"), "dd.MM.yyyy")),
    CAPITALIZE("capitalize", "converter.capitalize.code", Example.of(List.of("иван петров"), List.of("Иван петров"))),
    REMOVE_WHITESPACES(
            "removeWhitespaces",
            "converter.remove.whitespaces.code",
            Example.of(List.of("+7 912 345 67 89"), List.of("+79123456789"))),
    INVERSION("inversion", "converter.inversion.code", Example.of(List.of("Hello World"), List.of("hELLO wORLD"))),
    RANDOM_NUMERIC_REPLACE(
            "randomNumericReplace",
            "converter.random.numeric.replace.code",
            Example.of(List.of("кв. 12"), List.of("кв. 85"))),
    RANDOM_RUSSIAN_REPLACE(
            "randomRussianReplace",
            "converter.random.russian.replace.code",
            Example.of(List.of("ИД-7"), List.of("Жя-7"))),
    RANDOM_ENGLISH_REPLACE(
            "randomEnglishReplace",
            "converter.random.english.replace.code",
            Example.of(List.of("id-7"), List.of("Qx-7"))),
    RANDOM_REPLACE("randomReplace", "converter.random.replace.code", Example.of(List.of("ab"), List.of("7%"))),
    ADD_BEFORE("addBefore", "converter.add.before.code", Example.of(List.of("1042"), List.of("TCK-1042"), "TCK-")),
    ADD_AFTER("addAfter", "converter.add.after.code", Example.of(List.of("1500"), List.of("1500 RUB"), " RUB")),
    REPEAT("repeat", "converter.repeat.code", Example.of(List.of("ab"), List.of("ababab"), 3)),
    REVERSE("reverse", "converter.reverse.code", Example.of(List.of("stressed"), List.of("desserts"))),
    TRANSLIT("translit", "converter.translit.code", Example.of(List.of("Щукин Юрий"), List.of("Shchukin Yuriy"))),
    RANDOM_CASE("randomCase", "converter.random.case.code", Example.of(List.of("hello"), List.of("HeLlO"))),
    MD5("md5", "converter.md5.code", Example.of(List.of("password"), List.of("5f4dcc3b5aa765d61d8327deb882cf99"))),
    SHA256(
            "sha256",
            "converter.sha256.code",
            Example.of(
                    List.of("password"), List.of("5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8"))),
    MASK("mask", "converter.mask.code", Example.of(List.of("2200123456789019"), List.of("2200********9019"), 4, 4)),
    PAD_LEFT("padLeft", "converter.pad.left.code", Example.of(List.of("42"), List.of("000042"), 6, "0")),
    PAD_RIGHT("padRight", "converter.pad.right.code", Example.of(List.of("Итого"), List.of("Итого..."), 8, ".")),
    TRUNCATE("truncate", "converter.truncate.code", Example.of(List.of("Hello World"), List.of("Hello"), 5)),

    /**
     * Общие конвертеры
     */
    SHUFFLE("shuffle", "converter.shuffle.code", Example.of(List.of(1, 2, 3, 4, 5), List.of(3, 5, 1, 4, 2))),
    TO_NULL(
            "to_null",
            "converter.to_null.code",
            Example.of(List.of(1, 2, 3, 4, 5), Arrays.asList(1, null, 3, null, 5))
                    .withProbability(40)),
    SORT_ASC("sort_asc", "converter.sort_asc.code", Example.of(List.of(3, 1, 2), List.of(1, 2, 3))),
    SORT_DESC("sort_desc", "converter.sort_desc.code", Example.of(List.of(3, 1, 2), List.of(3, 2, 1)));

    private final String name;
    private final String descriptionCode;
    private final Example example;

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

    /**
     * Пример применения конвертера к колонке
     *
     * @param arguments аргументы конвертера
     * @param probability вероятность применения в процентах или null, если в примере она по умолчанию
     * @param values значения колонки до конвертера
     * @param results значения колонки после конвертера. Если конвертер работает случайно, это один из возможных
     *     результатов
     */
    public record Example(List<Object> arguments, Integer probability, List<?> values, List<?> results) {

        static Example of(List<?> values, List<?> results, Object... arguments) {
            return new Example(List.of(arguments), null, values, results);
        }

        Example withProbability(int probability) {
            return new Example(arguments, probability, values, results);
        }
    }
}
