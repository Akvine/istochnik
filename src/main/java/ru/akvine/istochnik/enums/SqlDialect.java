package ru.akvine.istochnik.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * Диалект SQL для выгрузки в формате SQL: как записываются имена, логические значения и строки
 * и сколько строк помещается в один INSERT
 */
@Getter
@AllArgsConstructor
public enum SqlDialect {
    POSTGRESQL("postgresql", List.of("postgres"), '"', '"', NameCase.LOWER, "TRUE", "FALSE", false, "", 0),
    MYSQL("mysql", List.of("mariadb"), '`', '`', NameCase.AS_IS, "TRUE", "FALSE", true, "", 0),
    // SQL Server принимает не больше 1000 строк в одном VALUES, а строки без N теряют символы вне кодовой страницы
    MSSQL("mssql", List.of("sqlserver"), '[', ']', NameCase.AS_IS, "1", "0", false, "N", 1000),
    // Oracle до версии 23 не принимает несколько строк в одном VALUES и не знает логических значений
    ORACLE("oracle", List.of(), '"', '"', NameCase.UPPER, "1", "0", false, "", 1),
    SQLITE("sqlite", List.of(), '"', '"', NameCase.AS_IS, "1", "0", false, "", 0);

    private final String name;

    /**
     * Другие названия, под которыми диалект принимается в запросе
     */
    private final List<String> aliases;

    private final char openQuote;
    private final char closeQuote;

    /**
     * К какому регистру приводятся имена перед заключением в кавычки. Имена в кавычках чувствительны к регистру,
     * поэтому они приводятся к тому регистру, в котором СУБД хранит имена, созданные без кавычек
     */
    private final NameCase nameCase;

    private final String trueLiteral;
    private final String falseLiteral;

    /**
     * Обратная косая черта в строке - начало управляющей последовательности, и её нужно удваивать
     */
    private final boolean backslashEscaped;

    /**
     * Что ставится перед открывающей кавычкой строки
     */
    private final String stringPrefix;

    /**
     * Сколько строк таблицы помещается в один INSERT. 0 - без ограничения
     */
    private final int maxRowsPerInsert;

    public enum NameCase {
        AS_IS,
        LOWER,
        UPPER
    }

    /**
     * @return диалект или null, если значение пустое
     */
    public static SqlDialect safeFrom(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        for (SqlDialect dialect : values()) {
            if (dialect.name.equalsIgnoreCase(value)
                    || dialect.aliases.stream().anyMatch(alias -> alias.equalsIgnoreCase(value))) {
                return dialect;
            }
        }

        String supported = Arrays.stream(values()).map(SqlDialect::getName).collect(Collectors.joining(", "));
        throw new UnsupportedOperationException(
                "SQL dialect = [" + value + "] is not supported by app. Supported dialects: [" + supported + "]");
    }

    /**
     * Имя таблицы в кавычках. Имя со схемой, например public.users, заключается в кавычки по частям
     */
    public String quoteTableName(String tableName) {
        return Arrays.stream(StringUtils.splitPreserveAllTokens(tableName, '.'))
                .map(this::quoteName)
                .collect(Collectors.joining("."));
    }

    public String quoteName(String name) {
        String folded =
                switch (nameCase) {
                    case LOWER -> name.toLowerCase(Locale.ROOT);
                    case UPPER -> name.toUpperCase(Locale.ROOT);
                    case AS_IS -> name;
                };
        // Закрывающая кавычка внутри имени удваивается
        String escaped = folded.replace(String.valueOf(closeQuote), String.valueOf(closeQuote) + closeQuote);
        return openQuote + escaped + closeQuote;
    }

    public String quoteString(String value) {
        String escaped = backslashEscaped ? value.replace("\\", "\\\\") : value;
        return stringPrefix + "'" + escaped.replace("'", "''") + "'";
    }

    public String booleanLiteral(boolean value) {
        return value ? trueLiteral : falseLiteral;
    }
}
