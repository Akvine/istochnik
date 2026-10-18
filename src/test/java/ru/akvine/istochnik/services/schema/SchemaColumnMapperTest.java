package ru.akvine.istochnik.services.schema;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;

public class SchemaColumnMapperTest {
    private static final String TABLE = "users";
    private static final int SIZE = 50;
    private static final LocalDate TODAY = LocalDate.of(2026, 3, 15);

    private final SchemaColumnMapper mapper = new SchemaColumnMapper();
    private List<String> warnings;

    @BeforeEach
    void setUp() {
        warnings = new ArrayList<>();
    }

    @Test
    @DisplayName("Integer types: sequence for keys, bounded random for other columns")
    void map_integer_types() {
        ColumnDto key = map(column("bigint").setUnique(true).setNotNull(true));
        assertThat(key.getType()).isEqualTo("int");
        assertThat(key.getGenerationStrategy()).isEqualTo("algorithm");
        assertThat(key.getConfig())
                .isEqualTo(new ConfigDto()
                        .setNotNull(true)
                        .setRangeType("shift")
                        .setStart("1")
                        .setStep("1"));

        assertThat(map(column("serial")).getConfig().getRangeType()).isEqualTo("shift");
        assertThat(map(column("int").setAutoIncrement(true)).getConfig().getRangeType())
                .isEqualTo("shift");

        ConfigDto regular = map(column("integer")).getConfig();
        assertThat(regular)
                .isEqualTo(new ConfigDto().setRangeType("random").setStart("1").setEnd("1000000"));

        assertThat(map(column("smallint")).getConfig().getEnd()).isEqualTo("32767");
        assertThat(map(column("int unsigned")).getType()).isEqualTo("int");
        assertThat(map(column("pg_catalog.int4")).getType()).isEqualTo("int");
        // numeric без дробной части - целое число, вмещающее не больше заданного количества цифр
        assertThat(map(column("numeric", "3")).getType()).isEqualTo("int");
        assertThat(map(column("numeric", "3")).getConfig().getEnd()).isEqualTo("1000");
        assertThat(map(column("number", "19", "0")).getType()).isEqualTo("int");
        assertThat(warnings).isEmpty();
    }

    @Test
    @DisplayName("Foreign key of integer type gets ids of the referenced table")
    void map_foreign_key() {
        ConfigDto config = map(column("bigint").setReference("roles(id)")).getConfig();

        assertThat(config.getRangeType()).isEqualTo("random");
        assertThat(config.getStart()).isEqualTo("1");
        assertThat(config.getEnd()).isEqualTo("51");
        assertThat(warnings)
                .containsExactly("Table [users], column [col]: references [roles(id)], values are generated in "
                        + "range [1, 50] in expectation of sequential ids of the referenced table");

        warnings.clear();
        assertThat(map(column("uuid").setReference("roles")).getType()).isEqualTo("uuid");
        assertThat(warnings)
                .containsExactly("Table [users], column [col]: references [roles], values are generated "
                        + "independently, referential integrity is not guaranteed");
    }

    @Test
    @DisplayName("Fractional types: scale and range are taken from precision")
    void map_fractional_types() {
        ColumnDto money = map(column("numeric", "12", "2").setNotNull(true));
        assertThat(money.getType()).isEqualTo("decimal");
        assertThat(money.getConfig())
                .isEqualTo(new ConfigDto()
                        .setNotNull(true)
                        .setRangeType("random")
                        .setStart("0.00")
                        .setEnd("10000.00"));

        ConfigDto rate = map(column("decimal", "5", "4")).getConfig();
        assertThat(rate.getStart()).isEqualTo("0.0000");
        assertThat(rate.getEnd()).isEqualTo("10.0000");

        assertThat(map(column("numeric")).getConfig().getEnd()).isEqualTo("10000.00");
        assertThat(map(column("java.sql.types.decimal", "19", "2")).getType()).isEqualTo("decimal");

        ConfigDto uniqueDecimal =
                map(column("numeric", "10", "2").setUnique(true)).getConfig();
        assertThat(uniqueDecimal.getRangeType()).isEqualTo("shift");
        assertThat(uniqueDecimal.getStart()).isEqualTo("0.01");
        assertThat(uniqueDecimal.getStep()).isEqualTo("0.01");

        // Уникальное число без точности - идентификатор
        ConfigDto numericKey = map(column("number").setUnique(true)).getConfig();
        assertThat(numericKey.getStart()).isEqualTo("1");
        assertThat(numericKey.getStep()).isEqualTo("1");

        ColumnDto real = map(column("double precision"));
        assertThat(real.getType()).isEqualTo("double");
        assertThat(real.getConfig())
                .isEqualTo(new ConfigDto().setRangeType("random").setStart("0").setEnd("1000"));
        assertThat(warnings).isEmpty();
    }

    @Test
    @DisplayName("Strings: length is limited by column length")
    void map_string_types() {
        ColumnDto name = map(column("varchar", "255").setNotNull(true).setUnique(true));
        assertThat(name.getType()).isEqualTo("str");
        assertThat(name.getConfig())
                .isEqualTo(new ConfigDto().setNotNull(true).setUnique(true).setLength(16));

        assertThat(map(column("char", "3")).getConfig().getLength()).isEqualTo(3);
        assertThat(map(column("text")).getConfig().getLength()).isEqualTo(16);
        assertThat(map(column("varchar2", "100 char")).getConfig().getLength()).isEqualTo(16);
        assertThat(map(column("character varying", "8")).getConfig().getLength())
                .isEqualTo(8);
        assertThat(map(column("bytea")).getType()).isEqualTo("str");
        assertThat(warnings).isEmpty();
    }

    @Test
    @DisplayName("Boolean, UUID, date and time types")
    void map_other_types() {
        assertThat(map(column("boolean")).getType()).isEqualTo("bool");
        assertThat(map(column("boolean")).getConfig().getRangeType()).isEqualTo("random");
        assertThat(map(column("tinyint", "1")).getType()).isEqualTo("bool");
        assertThat(map(column("tinyint")).getType()).isEqualTo("int");
        assertThat(map(column("tinyint")).getConfig().getEnd()).isEqualTo("127");

        ColumnDto uuid = map(column("uuid").setNotNull(true));
        assertThat(uuid.getType()).isEqualTo("uuid");
        assertThat(uuid.getConfig()).isEqualTo(new ConfigDto().setNotNull(true));

        ColumnDto date = map(column("date"));
        assertThat(date.getType()).isEqualTo("date");
        assertThat(date.getConfig())
                .isEqualTo(new ConfigDto()
                        .setRangeType("random")
                        .setStart("2025-03-15")
                        .setEnd("2026-03-15"));

        ColumnDto time = map(column("time without time zone"));
        assertThat(time.getType()).isEqualTo("time");
        assertThat(time.getConfig().getStart()).isEqualTo("00:00:00");
        assertThat(time.getConfig().getEnd()).isEqualTo("23:59:59");

        ColumnDto dateTime = map(column("timestamp", "6"));
        assertThat(dateTime.getType()).isEqualTo("datetime");
        assertThat(dateTime.getConfig().getStart()).isEqualTo("2025-03-15 00:00:00");
        assertThat(dateTime.getConfig().getEnd()).isEqualTo("2026-03-15 00:00:00");

        ColumnDto zoned = map(column("timestamp with time zone"));
        assertThat(zoned.getType()).isEqualTo("zoned_datetime");
        assertThat(zoned.getConfig().getStart()).isEqualTo("2025-03-15T00:00:00Z");
        assertThat(zoned.getConfig().getEnd()).isEqualTo("2026-03-15T00:00:00Z");
        assertThat(map(column("timestamptz")).getType()).isEqualTo("zoned_datetime");
        assertThat(warnings).isEmpty();
    }

    @Test
    @DisplayName("Enum becomes dictionary, JSON and arrays become constants")
    void map_enum_json_and_array() {
        ColumnDto status = map(column("order_status").setNotNull(true).setAllowedValues(List.of("NEW", "PAID")));
        assertThat(status.getType()).isEqualTo("str");
        assertThat(status.getGenerationStrategy()).isEqualTo("dictionary");
        assertThat(status.getConfig().getRangeType()).isEqualTo("random");
        assertThat(status.getConfig().isNotNull()).isTrue();
        assertThat(status.getConfig().getDictionaries()).containsExactly(Set.of("NEW", "PAID"));
        assertThat(warnings).isEmpty();

        ColumnDto json = map(column("jsonb").setNotNull(true));
        assertThat(json.getType()).isEqualTo("str");
        assertThat(json.getGenerationStrategy()).isEqualTo("constant");
        assertThat(json.getConfig()).isEqualTo(new ConfigDto().setConstant("{}"));

        ColumnDto array = map(new ColumnSchema().setName("col").setType(new ColumnType("text", List.of(), true)));
        assertThat(array.getGenerationStrategy()).isEqualTo("constant");
        assertThat(warnings)
                .containsExactly(
                        "Table [users], column [col]: type [jsonb] is filled with constant [{}]",
                        "Table [users], column [col]: type [text[]] is filled with constant [{}]");
    }

    @Test
    @DisplayName("Unknown type is generated as string with warning")
    void map_unknown_type() {
        ColumnDto geometry = map(column("geometry", "Point", "4326").setNotNull(true));

        assertThat(geometry.getType()).isEqualTo("str");
        assertThat(geometry.getGenerationStrategy()).isEqualTo("algorithm");
        assertThat(geometry.getConfig().getLength()).isEqualTo(16);
        assertThat(map(column("bit", "8")).getType()).isEqualTo("str");
        assertThat(warnings)
                .containsExactly(
                        "Table [users], column [col]: type [geometry(Point, 4326)] is not supported, "
                                + "random string is generated",
                        "Table [users], column [col]: type [bit(8)] is not supported, random string is generated");
    }

    private ColumnDto map(ColumnSchema column) {
        return mapper.map(TABLE, column, SIZE, TODAY, warnings);
    }

    private ColumnSchema column(String typeName, String... arguments) {
        return new ColumnSchema().setName("col").setType(new ColumnType(typeName, List.of(arguments), false));
    }
}
