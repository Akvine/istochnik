package ru.akvine.istochnik.services.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.akvine.istochnik.enums.SqlDialect;
import ru.akvine.istochnik.services.dto.Table;

@DisplayName("SQL file generator dialects tests")
class SqlFileTableGeneratorDialectsTest {

    @Test
    @DisplayName("Without dialect - names in upper case without quotes, as before dialects appeared")
    void without_dialect_output_is_unchanged() {
        assertThat(generate(1000, table(null)))
                .isEqualTo("INSERT INTO public.Users (ID, ORDER, NAME, ACTIVE) VALUES\n"
                        + "(1, 'first', 'O''Neil \\ dir', TRUE),\n"
                        + "(2, NULL, 'Иван', FALSE);\n");
    }

    @Test
    @DisplayName("PostgreSQL - names in lower case in double quotes")
    void postgresql() {
        assertThat(generate(1000, table(SqlDialect.POSTGRESQL)))
                .isEqualTo("INSERT INTO \"public\".\"users\" (\"id\", \"order\", \"name\", \"active\") VALUES\n"
                        + "(1, 'first', 'O''Neil \\ dir', TRUE),\n"
                        + "(2, NULL, 'Иван', FALSE);\n");
    }

    @Test
    @DisplayName("MySQL - names as in request in backticks, backslash is doubled")
    void mysql() {
        assertThat(generate(1000, table(SqlDialect.MYSQL)))
                .isEqualTo("INSERT INTO `public`.`Users` (`id`, `order`, `name`, `active`) VALUES\n"
                        + "(1, 'first', 'O''Neil \\\\ dir', TRUE),\n"
                        + "(2, NULL, 'Иван', FALSE);\n");
    }

    @Test
    @DisplayName("SQL Server - names in square brackets, strings with N, booleans as 1 and 0")
    void mssql() {
        assertThat(generate(1000, table(SqlDialect.MSSQL)))
                .isEqualTo("INSERT INTO [public].[Users] ([id], [order], [name], [active]) VALUES\n"
                        + "(1, N'first', N'O''Neil \\ dir', 1),\n"
                        + "(2, NULL, N'Иван', 0);\n");
    }

    @Test
    @DisplayName("Oracle - names in upper case in double quotes, one row per INSERT, booleans as 1 and 0")
    void oracle() {
        assertThat(generate(1000, table(SqlDialect.ORACLE)))
                .isEqualTo("INSERT INTO \"PUBLIC\".\"USERS\" (\"ID\", \"ORDER\", \"NAME\", \"ACTIVE\") VALUES\n"
                        + "(1, 'first', 'O''Neil \\ dir', 1);\n"
                        + "INSERT INTO \"PUBLIC\".\"USERS\" (\"ID\", \"ORDER\", \"NAME\", \"ACTIVE\") VALUES\n"
                        + "(2, NULL, 'Иван', 0);\n");
    }

    @Test
    @DisplayName("SQLite - names as in request in double quotes, booleans as 1 and 0")
    void sqlite() {
        assertThat(generate(1000, table(SqlDialect.SQLITE)))
                .isEqualTo("INSERT INTO \"public\".\"Users\" (\"id\", \"order\", \"name\", \"active\") VALUES\n"
                        + "(1, 'first', 'O''Neil \\ dir', 1),\n"
                        + "(2, NULL, 'Иван', 0);\n");
    }

    @Test
    @DisplayName("Quote inside a name is doubled, so the name can't break the statement")
    void quote_inside_name_is_escaped() {
        Table table = new Table(1, "we\"ird]`t");
        table.addColumn("a\"b]c`d", List.of(1L));

        table.setSqlDialect(SqlDialect.POSTGRESQL);
        assertThat(generate(1000, table)).startsWith("INSERT INTO \"we\"\"ird]`t\" (\"a\"\"b]c`d\") VALUES");
        table.setSqlDialect(SqlDialect.MSSQL);
        assertThat(generate(1000, table)).startsWith("INSERT INTO [we\"ird]]`t] ([a\"b]]c`d]) VALUES");
        table.setSqlDialect(SqlDialect.MYSQL);
        assertThat(generate(1000, table)).startsWith("INSERT INTO `we\"ird]``t` (`a\"b]c``d`) VALUES");
    }

    @Test
    @DisplayName("SQL Server takes at most 1000 rows per INSERT even if the setting allows more")
    void mssql_limits_rows_per_insert() {
        String sql = generate(5000, numbers(2500, SqlDialect.MSSQL));

        assertThat(sql.lines().filter(line -> line.startsWith("INSERT INTO"))).hasSize(3);
        assertThat(sql.lines().filter(line -> line.startsWith("("))).hasSize(2500);
        // Настройка меньше лимита диалекта - действует настройка
        assertThat(generate(100, numbers(250, SqlDialect.MSSQL)).lines().filter(line -> line.startsWith("INSERT")))
                .hasSize(3);
        // Разбиение отключено настройкой - действует лимит диалекта
        assertThat(generate(0, numbers(2500, SqlDialect.MSSQL)).lines().filter(line -> line.startsWith("INSERT")))
                .hasSize(3);
    }

    @Test
    @DisplayName("Dialect is found by name or alias in any case, unknown dialect is an error")
    void dialect_is_resolved_by_name_or_alias() {
        assertThat(SqlDialect.safeFrom("PostgreSQL")).isEqualTo(SqlDialect.POSTGRESQL);
        assertThat(SqlDialect.safeFrom("postgres")).isEqualTo(SqlDialect.POSTGRESQL);
        assertThat(SqlDialect.safeFrom("mariadb")).isEqualTo(SqlDialect.MYSQL);
        assertThat(SqlDialect.safeFrom("sqlserver")).isEqualTo(SqlDialect.MSSQL);
        assertThat(SqlDialect.safeFrom(" ")).isNull();
        assertThatThrownBy(() -> SqlDialect.safeFrom("db2"))
                .hasMessage("SQL dialect = [db2] is not supported by app. "
                        + "Supported dialects: [postgresql, mysql, mssql, oracle, sqlite]");
    }

    private Table table(SqlDialect dialect) {
        Table table = new Table(2, "public.Users");
        table.addColumn("id", List.of(1L, 2L));
        // Зарезервированное слово: без кавычек такое имя колонки не принимает ни одна СУБД
        table.addColumn("order", Arrays.asList("first", null));
        table.addColumn("name", List.of("O'Neil \\ dir", "Иван"));
        table.addColumn("active", List.of(true, false));
        table.setSqlDialect(dialect);
        return table;
    }

    private Table numbers(int rowsCount, SqlDialect dialect) {
        Table table = new Table(rowsCount, "numbers");
        table.addColumn("value", LongStream.rangeClosed(1, rowsCount).boxed().toList());
        table.setSqlDialect(dialect);
        return table;
    }

    private String generate(int batchSize, Table table) {
        SqlFileTableGenerator generator = new SqlFileTableGenerator();
        ReflectionTestUtils.setField(generator, "insertBatchSize", batchSize);
        return new String(generator.generate(table), StandardCharsets.UTF_8);
    }
}
