package ru.akvine.istochnik.services.schema;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;

/**
 * Подбирает для колонки схемы параметры генерации: стратегию, тип и диапазон значений.
 * <p>
 * Результат - заготовка, которая проходит проверку генератора и даёт значения, подходящие колонке по типу.
 * Смысл данных схеме неизвестен, поэтому диапазоны взяты условные - их уточняет пользователь.
 */
@Component
public class SchemaColumnMapper {
    private static final int DEFAULT_STRING_LENGTH = 16;
    private static final int DEFAULT_INTEGER_END = 1_000_000;
    private static final int TINY_INTEGER_END = 127;
    private static final int SMALL_INTEGER_END = 32_767;
    private static final String DEFAULT_DOUBLE_END = "1000";
    private static final int DEFAULT_DECIMAL_SCALE = 2;
    private static final int MAX_DECIMAL_INTEGER_DIGITS = 4;
    private static final int DATE_RANGE_YEARS = 1;

    private static final String ZERO = "0";
    private static final String ONE = "1";
    private static final String DAY_START = " 00:00:00";
    private static final String UTC_DAY_START = "T00:00:00Z";
    private static final String TIME_START = "00:00:00";
    private static final String TIME_END = "23:59:59";

    // Пустой объект JSON и одновременно пустой массив PostgreSQL
    private static final String EMPTY_VALUE = "{}";

    // Уточнения типа, которые на выбор генератора не влияют: int unsigned zerofill (MySQL)
    private static final Set<String> TYPE_MODIFIERS = Set.of("unsigned", "signed", "zerofill");
    // Liquibase допускает запись java.sql.Types.VARCHAR, PostgreSQL - pg_catalog.int4
    private static final List<String> TYPE_PREFIXES = List.of("java.sql.types.", "pg_catalog.");

    private static final Set<String> SERIAL_TYPES =
            Set.of("serial", "serial4", "smallserial", "serial2", "bigserial", "serial8");
    private static final Set<String> SMALL_INTEGER_TYPES = Set.of("smallint", "int2", "smallserial", "serial2");
    private static final String TINY_INTEGER_TYPE = "tinyint";
    private static final String BIT_TYPE = "bit";

    private static final Map<String, Kind> KINDS = new HashMap<>();

    static {
        register(
                Kind.INTEGER,
                "tinyint",
                "smallint",
                "int2",
                "mediumint",
                "int",
                "integer",
                "int4",
                "bigint",
                "int8",
                "long",
                "serial",
                "serial4",
                "smallserial",
                "serial2",
                "bigserial",
                "serial8");
        register(
                Kind.DOUBLE,
                "real",
                "float",
                "float4",
                "float8",
                "double",
                "double precision",
                "binary_float",
                "binary_double");
        register(Kind.DECIMAL, "numeric", "decimal", "dec", "number", "money", "smallmoney", "currency");
        register(Kind.BOOLEAN, "boolean", "bool", BIT_TYPE);
        register(
                Kind.STRING,
                "char",
                "character",
                "bpchar",
                "nchar",
                "national char",
                "national character",
                "varchar",
                "varchar2",
                "nvarchar",
                "nvarchar2",
                "character varying",
                "national varchar",
                "national character varying",
                "text",
                "tinytext",
                "mediumtext",
                "longtext",
                "ntext",
                "clob",
                "nclob",
                "citext",
                "string",
                "longvarchar",
                // Двоичные данные заполняются теми же случайными строками: это допустимое значение для таких колонок
                "bytea",
                "blob",
                "tinyblob",
                "mediumblob",
                "longblob",
                "binary",
                "varbinary",
                "raw");
        register(Kind.UUID, "uuid", "uniqueidentifier");
        register(Kind.DATE, "date");
        register(Kind.TIME, "time", "time without time zone", "time with time zone", "timetz");
        register(Kind.DATETIME, "timestamp", "timestamp without time zone", "datetime", "datetime2", "smalldatetime");
        register(
                Kind.ZONED_DATETIME,
                "timestamptz",
                "timestamp with time zone",
                "timestamp with local time zone",
                "datetimeoffset");
        register(Kind.JSON, "json", "jsonb");
    }

    /**
     * @param size     количество строк, которое будет сгенерировано: от него зависит диапазон внешних ключей
     * @param today    текущая дата: от неё отсчитываются диапазоны дат
     * @param warnings сюда добавляются сообщения о том, что в колонке учтено неточно
     */
    public ColumnDto map(String tableName, ColumnSchema column, int size, LocalDate today, List<String> warnings) {
        ColumnType type = column.getType();
        String typeName = normalize(type.name());
        ConfigDto config = new ConfigDto().setNotNull(column.isNotNull());
        ColumnDto result = new ColumnDto()
                .setName(column.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(config);

        Kind kind = resolveKind(typeName, column);
        switch (kind) {
            case ENUM -> {
                result.setType(BaseType.STRING.getValue())
                        .setGenerationStrategy(GenerationStrategy.DICTIONARY.getName());
                config.setRangeType(RangeType.RANDOM.getType())
                        .setDictionaries(Collections.singleton(new LinkedHashSet<>(column.getAllowedValues())));
            }
            case INTEGER -> {
                result.setType(BaseType.INTEGER.getValue());
                fillInteger(config, column, typeName, size);
            }
            case DOUBLE -> {
                result.setType(BaseType.DOUBLE.getValue());
                fillRandomRange(config, column, ZERO, DEFAULT_DOUBLE_END);
            }
            case DECIMAL -> {
                result.setType(CustomType.DECIMAL.getName());
                fillDecimal(config, column);
            }
            case BOOLEAN -> {
                result.setType(BaseType.BOOLEAN.getValue());
                config.setRangeType(RangeType.RANDOM.getType());
            }
            case STRING -> {
                result.setType(BaseType.STRING.getValue());
                fillString(config, column);
            }
            case UUID -> result.setType(CustomType.UUID.getName());
            case DATE -> {
                result.setType(CustomType.DATE.getName());
                fillRandomRange(config, column, rangeStart(today).toString(), today.toString());
            }
            case TIME -> {
                result.setType(CustomType.TIME.getName());
                fillRandomRange(config, column, TIME_START, TIME_END);
            }
            case DATETIME -> {
                result.setType(CustomType.DATETIME.getName());
                fillRandomRange(config, column, rangeStart(today) + DAY_START, today + DAY_START);
            }
            case ZONED_DATETIME -> {
                result.setType(CustomType.ZONED_DATETIME.getName());
                fillRandomRange(config, column, rangeStart(today) + UTC_DAY_START, today + UTC_DAY_START);
            }
            case JSON, ARRAY -> {
                fillConstant(result, EMPTY_VALUE);
                warnings.add(describe(tableName, column) + ": type [" + type + "] is filled with constant ["
                        + EMPTY_VALUE + "]");
            }
            default -> {
                result.setType(BaseType.STRING.getValue());
                fillString(config, column);
                warnings.add(describe(tableName, column) + ": type [" + type
                        + "] is not supported, random string is generated");
            }
        }

        if (column.getReference() != null) {
            warnings.add(describeReference(tableName, column, kind, size));
        }
        return result;
    }

    private Kind resolveKind(String typeName, ColumnSchema column) {
        ColumnType type = column.getType();
        if (type.array()) {
            return Kind.ARRAY;
        }
        if (!column.getAllowedValues().isEmpty()) {
            return Kind.ENUM;
        }

        Kind kind = KINDS.getOrDefault(typeName, Kind.UNKNOWN);
        Integer firstArgument = type.intArgument(0);
        Integer secondArgument = type.intArgument(1);

        // tinyint(1) в MySQL - логический тип
        if (TINY_INTEGER_TYPE.equals(typeName) && Integer.valueOf(1).equals(firstArgument)) {
            return Kind.BOOLEAN;
        }
        // bit(n) при n > 1 - битовая строка, а не логическое значение
        if (BIT_TYPE.equals(typeName) && firstArgument != null && firstArgument > 1) {
            return Kind.UNKNOWN;
        }
        // numeric(10) и numeric(10, 0) хранят целые числа
        if (kind == Kind.DECIMAL && firstArgument != null && (secondArgument == null || secondArgument <= 0)) {
            return Kind.INTEGER;
        }
        return kind;
    }

    private void fillInteger(ConfigDto config, ColumnSchema column, String typeName, int size) {
        if (column.isUnique() || column.isAutoIncrement() || SERIAL_TYPES.contains(typeName)) {
            // Последовательность уникальна по построению и не содержит null
            config.setRangeType(RangeType.SHIFT.getType()).setStart(ONE).setStep(ONE);
        } else if (column.getReference() != null) {
            // Если таблица, на которую ссылается ключ, сгенерирована тем же способом и с тем же числом строк,
            // её идентификаторы - числа от 1 до size
            int end = Math.max(size, 1) + 1;
            config.setRangeType(RangeType.RANDOM.getType()).setStart(ONE).setEnd(String.valueOf(end));
        } else {
            int end = integerEnd(typeName, column.getType());
            config.setRangeType(RangeType.RANDOM.getType()).setStart(ONE).setEnd(String.valueOf(end));
        }
    }

    // Правая граница не включается в диапазон
    private int integerEnd(String typeName, ColumnType type) {
        if (TINY_INTEGER_TYPE.equals(typeName)) {
            return TINY_INTEGER_END;
        }
        if (SMALL_INTEGER_TYPES.contains(typeName)) {
            return SMALL_INTEGER_END;
        }

        // numeric(p) вмещает p цифр
        Integer precision = KINDS.get(typeName) == Kind.DECIMAL ? type.intArgument(0) : null;
        if (precision != null && Math.pow(10, precision) < DEFAULT_INTEGER_END) {
            return (int) Math.pow(10, precision);
        }
        return DEFAULT_INTEGER_END;
    }

    private void fillDecimal(ConfigDto config, ColumnSchema column) {
        Integer precision = column.getType().intArgument(0);
        Integer scale = column.getType().intArgument(1);

        // Уникальные значения без заданной точности - скорее всего идентификаторы: 1, 2, 3...
        if (column.isUnique() && precision == null) {
            config.setRangeType(RangeType.SHIFT.getType()).setStart(ONE).setStep(ONE);
            return;
        }

        int fraction = scale == null ? DEFAULT_DECIMAL_SCALE : scale;
        if (column.isUnique()) {
            // Шаг в одну единицу младшего разряда: 0.01, 0.02, 0.03...
            String unit = ZERO + "." + ZERO.repeat(fraction - 1) + ONE;
            config.setRangeType(RangeType.SHIFT.getType()).setStart(unit).setStep(unit);
            return;
        }

        int integerDigits = precision == null
                ? MAX_DECIMAL_INTEGER_DIGITS
                : Math.max(0, Math.min(precision - fraction, MAX_DECIMAL_INTEGER_DIGITS));
        String fractionZeros = "." + ZERO.repeat(fraction);
        config.setRangeType(RangeType.RANDOM.getType())
                .setStart(ZERO + fractionZeros)
                .setEnd(ONE + ZERO.repeat(integerDigits) + fractionZeros);
    }

    private void fillString(ConfigDto config, ColumnSchema column) {
        Integer maxLength = column.getType().intArgument(0);
        int length = maxLength == null || maxLength <= 0 ? DEFAULT_STRING_LENGTH : maxLength;
        config.setLength(Math.min(length, DEFAULT_STRING_LENGTH)).setUnique(column.isUnique());
    }

    private void fillRandomRange(ConfigDto config, ColumnSchema column, String start, String end) {
        config.setRangeType(RangeType.RANDOM.getType())
                .setStart(start)
                .setEnd(end)
                .setUnique(column.isUnique());
    }

    private void fillConstant(ColumnDto result, String constant) {
        result.setType(BaseType.STRING.getValue())
                .setGenerationStrategy(GenerationStrategy.CONSTANT.getName())
                .setConfig(new ConfigDto().setConstant(constant));
    }

    private LocalDate rangeStart(LocalDate today) {
        return today.minusYears(DATE_RANGE_YEARS);
    }

    private String describeReference(String tableName, ColumnSchema column, Kind kind, int size) {
        String description = describe(tableName, column) + ": references [" + column.getReference() + "], ";
        if (kind == Kind.INTEGER) {
            return description + "values are generated in range [1, " + Math.max(size, 1)
                    + "] in expectation of sequential ids of the referenced table";
        }
        return description + "values are generated independently, referential integrity is not guaranteed";
    }

    private String describe(String tableName, ColumnSchema column) {
        return "Table [" + tableName + "], column [" + column.getName() + "]";
    }

    private String normalize(String typeName) {
        String normalized = typeName.toLowerCase(Locale.ROOT);
        for (String prefix : TYPE_PREFIXES) {
            normalized = StringUtils.removeStart(normalized, prefix);
        }

        return Arrays.stream(normalized.split(" "))
                .filter(word -> !TYPE_MODIFIERS.contains(word))
                .collect(Collectors.joining(" "));
    }

    private static void register(Kind kind, String... typeNames) {
        for (String typeName : typeNames) {
            KINDS.put(typeName, kind);
        }
    }

    private enum Kind {
        INTEGER,
        DOUBLE,
        DECIMAL,
        BOOLEAN,
        STRING,
        UUID,
        DATE,
        TIME,
        DATETIME,
        ZONED_DATETIME,
        JSON,
        ENUM,
        ARRAY,
        UNKNOWN
    }
}
