package ru.akvine.istochnik.services.schema;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SqlSchemaParserTest {
    private final SqlSchemaParser parser = new SqlSchemaParser();

    @Test
    @DisplayName("Columns, types and inline constraints are read from CREATE TABLE")
    void parse_create_table() {
        ParsedSchema schema = parser.parse(
                """
                --liquibase formatted sql
                --changeset akvine:1
                CREATE TABLE IF NOT EXISTS public.users (
                    id          BIGSERIAL PRIMARY KEY,
                    email       VARCHAR(255) NOT NULL UNIQUE,
                    full_name   character varying(100) NOT NULL,
                    balance     NUMERIC(12, 2) NOT NULL DEFAULT 0,
                    rating      double precision,
                    created_at  TIMESTAMP(6) WITH TIME ZONE DEFAULT now() NOT NULL,
                    "Tags"      text[],
                    age         smallint CHECK (age > 0 AND age IS NOT NULL)
                );
                """);

        assertThat(schema.tables()).hasSize(1);
        TableSchema users = schema.tables().get(0);
        assertThat(users.getName()).isEqualTo("public.users");
        assertThat(users.getColumns())
                .extracting(ColumnSchema::getName)
                .containsExactly("id", "email", "full_name", "balance", "rating", "created_at", "Tags", "age");

        assertThat(users.findColumn("id").getType().name()).isEqualTo("bigserial");
        assertThat(users.findColumn("id").isNotNull()).isTrue();
        assertThat(users.findColumn("id").isUnique()).isTrue();

        assertThat(users.findColumn("email").getType()).isEqualTo(new ColumnType("varchar", List.of("255"), false));
        assertThat(users.findColumn("email").isNotNull()).isTrue();
        assertThat(users.findColumn("email").isUnique()).isTrue();

        assertThat(users.findColumn("full_name").getType().name()).isEqualTo("character varying");
        assertThat(users.findColumn("balance").getType().arguments()).containsExactly("12", "2");
        assertThat(users.findColumn("rating").getType().name()).isEqualTo("double precision");
        assertThat(users.findColumn("rating").isNotNull()).isFalse();

        assertThat(users.findColumn("created_at").getType())
                .isEqualTo(new ColumnType("timestamp with time zone", List.of("6"), false));
        assertThat(users.findColumn("created_at").isNotNull()).isTrue();

        assertThat(users.findColumn("tags").getType()).isEqualTo(new ColumnType("text", List.of(), true));
        // NOT NULL внутри CHECK к самой колонке не относится
        assertThat(users.findColumn("age").isNotNull()).isFalse();
    }

    @Test
    @DisplayName("Comments, string literals and dollar-quoted bodies don't break statements")
    void parse_ignores_comments_and_strings() {
        ParsedSchema schema = parser.parse(
                """
                /* первая таблица; с точкой с запятой в комментарии */
                CREATE TABLE a (
                    id int NOT NULL, -- комментарий, с запятой
                    note text DEFAULT 'x; y, (z) -- не комментарий' NOT NULL
                );
                CREATE FUNCTION f() RETURNS void AS $body$
                BEGIN
                    CREATE TABLE inside_function (id int);
                END;
                $body$ LANGUAGE plpgsql;
                INSERT INTO a (id, note) VALUES (1, 'CREATE TABLE fake (id int);');
                create table b (id int)
                """);

        assertThat(schema.tables()).extracting(TableSchema::getName).containsExactly("a", "b");
        assertThat(schema.tables().get(0).getColumns())
                .extracting(ColumnSchema::getName)
                .containsExactly("id", "note");
        assertThat(schema.tables().get(0).findColumn("note").isNotNull()).isTrue();
    }

    @Test
    @DisplayName("Table constraints define keys, composite key makes its first column unique")
    void parse_table_constraints() {
        ParsedSchema schema = parser.parse(
                """
                CREATE TABLE roles (id int, code varchar(10), CONSTRAINT roles_pk PRIMARY KEY (id), UNIQUE (code));
                CREATE TABLE user_roles (
                    user_id bigint,
                    role_id int,
                    PRIMARY KEY (user_id, role_id),
                    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
                );
                """);

        TableSchema roles = schema.tables().get(0);
        assertThat(roles.findColumn("id").isUnique()).isTrue();
        assertThat(roles.findColumn("id").isNotNull()).isTrue();
        assertThat(roles.findColumn("code").isUnique()).isTrue();
        assertThat(roles.findColumn("code").isNotNull()).isFalse();

        TableSchema userRoles = schema.tables().get(1);
        assertThat(userRoles.findColumn("user_id").isUnique()).isTrue();
        assertThat(userRoles.findColumn("user_id").isNotNull()).isTrue();
        assertThat(userRoles.findColumn("role_id").isUnique()).isFalse();
        assertThat(userRoles.findColumn("role_id").isNotNull()).isTrue();
        assertThat(userRoles.findColumn("role_id").getReference()).isEqualTo("roles(id)");
        assertThat(schema.warnings())
                .containsExactly("Table [user_roles]: uniqueness of composite key [user_id, role_id] "
                        + "is provided by column [user_id] only");
    }

    @Test
    @DisplayName("Sequences, identity and computed columns are recognized")
    void parse_generated_columns() {
        ParsedSchema schema = parser.parse(
                """
                CREATE TABLE t (
                    a bigint GENERATED ALWAYS AS IDENTITY,
                    b bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                    c integer DEFAULT nextval('t_c_seq'::regclass) NOT NULL,
                    d numeric(10, 2) GENERATED ALWAYS AS (e * 2) STORED,
                    e numeric(10, 2),
                    f int AUTO_INCREMENT,
                    g int REFERENCES other
                );
                """);

        TableSchema table = schema.tables().get(0);
        assertThat(table.findColumn("a").isDatabaseGenerated()).isTrue();
        assertThat(table.findColumn("b").isDatabaseGenerated()).isFalse();
        assertThat(table.findColumn("b").isAutoIncrement()).isTrue();
        assertThat(table.findColumn("c").isAutoIncrement()).isTrue();
        assertThat(table.findColumn("c").isNotNull()).isTrue();
        assertThat(table.findColumn("d").isDatabaseGenerated()).isTrue();
        assertThat(table.findColumn("e").isDatabaseGenerated()).isFalse();
        assertThat(table.findColumn("f").isAutoIncrement()).isTrue();
        assertThat(table.findColumn("g").getReference()).isEqualTo("other");
    }

    @Test
    @DisplayName("ALTER TABLE, unique index and enum type change tables created earlier")
    void parse_following_statements() {
        ParsedSchema schema = parser.parse(
                """
                CREATE TYPE public.order_status AS ENUM ('NEW', 'PAID');
                CREATE TABLE orders (id bigint NOT NULL, status order_status, code varchar(20), legacy text, slug text);
                ALTER TABLE ONLY public.orders ADD CONSTRAINT orders_pkey PRIMARY KEY (id);
                ALTER TABLE orders
                    ADD COLUMN IF NOT EXISTS comment varchar(500) NOT NULL,
                    DROP COLUMN legacy,
                    ALTER COLUMN code SET NOT NULL,
                    ADD CONSTRAINT orders_user_fk FOREIGN KEY (id) REFERENCES users (id);
                CREATE UNIQUE INDEX orders_code_idx ON orders USING btree (code);
                CREATE UNIQUE INDEX orders_slug_idx ON orders (slug) WHERE slug IS NOT NULL;
                CREATE INDEX orders_status_idx ON orders (status);
                ALTER TABLE unknown_table ADD COLUMN x int;
                """);

        assertThat(schema.tables()).hasSize(1);
        TableSchema orders = schema.tables().get(0);
        assertThat(orders.getColumns())
                .extracting(ColumnSchema::getName)
                .containsExactly("id", "status", "code", "slug", "comment");
        assertThat(orders.findColumn("id").isUnique()).isTrue();
        assertThat(orders.findColumn("id").getReference()).isEqualTo("users(id)");
        assertThat(orders.findColumn("status").getAllowedValues()).containsExactly("NEW", "PAID");
        assertThat(orders.findColumn("code").isNotNull()).isTrue();
        assertThat(orders.findColumn("code").isUnique()).isTrue();
        // Частичный индекс не делает колонку уникальной
        assertThat(orders.findColumn("slug").isUnique()).isFalse();
        assertThat(orders.findColumn("comment").isNotNull()).isTrue();
    }

    @Test
    @DisplayName("MySQL and SQL Server syntax is tolerated")
    void parse_other_dialects() {
        ParsedSchema schema = parser.parse(
                """
                CREATE TABLE `shop`.`items` (
                  `id` int unsigned NOT NULL AUTO_INCREMENT,
                  `key` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT 'ключ, не индекс',
                  `state` enum('NEW','DONE') NOT NULL DEFAULT 'NEW',
                  `flag` tinyint(1) DEFAULT NULL,
                  PRIMARY KEY (`id`),
                  UNIQUE KEY `uk_key` (`key`),
                  KEY `idx_state` (`state`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                CREATE TABLE [dbo].[accounts] ([id] INT IDENTITY(1,1) NOT NULL, [name] NVARCHAR(50) NULL);
                """);

        TableSchema items = schema.tables().get(0);
        assertThat(items.getName()).isEqualTo("shop.items");
        assertThat(items.getColumns()).extracting(ColumnSchema::getName).containsExactly("id", "key", "state", "flag");
        assertThat(items.findColumn("id").getType().name()).isEqualTo("int unsigned");
        assertThat(items.findColumn("id").isAutoIncrement()).isTrue();
        assertThat(items.findColumn("key").getType()).isEqualTo(new ColumnType("varchar", List.of("64"), false));
        assertThat(items.findColumn("key").isUnique()).isTrue();
        assertThat(items.findColumn("state").getAllowedValues()).containsExactly("NEW", "DONE");
        assertThat(items.findColumn("flag").isNotNull()).isFalse();

        TableSchema accounts = schema.tables().get(1);
        assertThat(accounts.getName()).isEqualTo("dbo.accounts");
        assertThat(accounts.findColumn("id").isAutoIncrement()).isTrue();
        assertThat(accounts.findColumn("id").isNotNull()).isTrue();
        assertThat(accounts.findColumn("name").getType().name()).isEqualTo("nvarchar");
    }

    @Test
    @DisplayName("Any broken script is parsed without errors")
    void parse_broken_script() {
        String script =
                """
                CREATE TYPE status AS ENUM ('A', 'B');
                CREATE TABLE IF NOT EXISTS "s"."t" (
                    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                    code varchar(10) DEFAULT 'x;' NOT NULL REFERENCES other (code),
                    tags text[], amount numeric(10, 2) CHECK (amount > 0), state status,
                    CONSTRAINT uq UNIQUE (code, state), FOREIGN KEY (state) REFERENCES states (id)
                );
                ALTER TABLE s.t ADD COLUMN x int, DROP COLUMN tags, ALTER COLUMN x SET NOT NULL;
                CREATE UNIQUE INDEX idx ON s.t USING btree (x) WHERE x > 0;
                """;

        // Схема приходит от клиента: скрипт, оборванный в любом месте, не должен приводить к исключению
        for (int length = 0; length <= script.length(); length++) {
            String prefix = script.substring(0, length);
            assertThatCode(() -> parser.parse(prefix)).doesNotThrowAnyException();
            assertThatCode(() -> parser.parse(prefix + ")")).doesNotThrowAnyException();
        }
    }

    @Test
    @DisplayName("Parsing time grows linearly with script size")
    void parse_large_script() {
        int count = 100_000;
        String columns = IntStream.range(0, count)
                .mapToObj(index -> "c" + index + " int NOT NULL")
                .collect(Collectors.joining(", "));
        String manyColumns = "CREATE TABLE t (" + columns + ");";
        String manyTables = "CREATE TABLE t (id int); ALTER TABLE t ADD COLUMN c int;".repeat(count / 10);
        String manyBrackets = "CREATE TABLE t (id int " + "[".repeat(count * 4) + ");";

        assertTimeout(Duration.ofSeconds(20), () -> {
            assertThat(parser.parse(manyColumns).tables().get(0).getColumns()).hasSize(count);
            assertThat(parser.parse(manyTables).tables()).hasSize(1);
            assertThat(parser.parse(manyBrackets).tables()).hasSize(1);
        });
    }

    @Test
    @DisplayName("Table without column list is skipped with warning")
    void parse_table_without_columns() {
        ParsedSchema schema = parser.parse("CREATE TABLE copy AS SELECT * FROM source; CREATE VIEW v AS SELECT 1;");

        assertThat(schema.tables()).isEmpty();
        assertThat(schema.warnings())
                .containsExactly("Table [copy]: columns are not listed in CREATE TABLE, table is skipped");
    }
}
