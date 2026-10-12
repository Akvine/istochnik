package ru.akvine.istochnik.services.schema;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.akvine.istochnik.exceptions.SchemaConversionException;

public class XmlSchemaParserTest {
    private static final String CHANGE_LOG_START =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                               xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                                   http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.20.xsd">
            """;
    private static final String CHANGE_LOG_END = "</databaseChangeLog>";

    private final XmlSchemaParser parser = new XmlSchemaParser();

    @Test
    @DisplayName("Columns, types and constraints are read from createTable")
    void parse_create_table() {
        ParsedSchema schema = parser.parse(
                changeLog(
                        """
                <changeSet id="1" author="akvine">
                    <createTable tableName="users" schemaName="public">
                        <column name="id" type="BIGINT" autoIncrement="true">
                            <constraints primaryKey="true" nullable="false"/>
                        </column>
                        <column name="email" type="VARCHAR(255)">
                            <constraints nullable="false" unique="true"/>
                        </column>
                        <column name="balance" type="java.sql.Types.DECIMAL(19, 2)"/>
                        <column name="role_id" type="INT">
                            <constraints foreignKeyName="fk_users_roles" references="roles(id)"/>
                        </column>
                        <column name="created_at" type="TIMESTAMP WITH TIME ZONE" defaultValueComputed="now()"/>
                        <column name="payload" type="${json.type}"/>
                    </createTable>
                    <rollback>
                        <createTable tableName="must_be_ignored">
                            <column name="id" type="INT"/>
                        </createTable>
                    </rollback>
                </changeSet>
                """));

        assertThat(schema.tables()).hasSize(1);
        TableSchema users = schema.tables().get(0);
        assertThat(users.getName()).isEqualTo("public.users");
        assertThat(users.getColumns())
                .extracting(ColumnSchema::getName)
                .containsExactly("id", "email", "balance", "role_id", "created_at", "payload");

        assertThat(users.findColumn("id").getType().name()).isEqualTo("bigint");
        assertThat(users.findColumn("id").isAutoIncrement()).isTrue();
        assertThat(users.findColumn("id").isNotNull()).isTrue();
        assertThat(users.findColumn("id").isUnique()).isTrue();

        assertThat(users.findColumn("email").getType()).isEqualTo(new ColumnType("varchar", List.of("255"), false));
        assertThat(users.findColumn("email").isNotNull()).isTrue();
        assertThat(users.findColumn("email").isUnique()).isTrue();

        assertThat(users.findColumn("balance").getType())
                .isEqualTo(new ColumnType("java.sql.types.decimal", List.of("19", "2"), false));
        assertThat(users.findColumn("balance").isNotNull()).isFalse();
        assertThat(users.findColumn("role_id").getReference()).isEqualTo("roles(id)");
        assertThat(users.findColumn("created_at").getType().name()).isEqualTo("timestamp with time zone");
        // Подстановка Liquibase не является типом SQL: остаётся как есть
        assertThat(users.findColumn("payload").getType().name()).isEqualTo("${json.type}");
    }

    @Test
    @DisplayName("Following changes modify tables created earlier")
    void parse_following_changes() {
        ParsedSchema schema = parser.parse(
                changeLog(
                        """
                <changeSet id="1" author="akvine">
                    <createTable tableName="orders">
                        <column name="id" type="BIGINT"/>
                        <column name="code" type="VARCHAR(20)"/>
                        <column name="user_id" type="BIGINT"/>
                        <column name="legacy" type="CLOB"/>
                        <column name="slug" type="VARCHAR(50)"/>
                    </createTable>
                </changeSet>
                <changeSet id="2" author="akvine">
                    <addPrimaryKey tableName="orders" columnNames="id" constraintName="orders_pk"/>
                    <addUniqueConstraint tableName="orders" columnNames="code"/>
                    <addNotNullConstraint tableName="orders" columnName="code" columnDataType="VARCHAR(20)"/>
                    <addForeignKeyConstraint baseTableName="orders" baseColumnNames="user_id"
                                             constraintName="fk_orders_users"
                                             referencedTableName="users" referencedColumnNames="id"/>
                    <addColumn tableName="orders">
                        <column name="comment" type="VARCHAR(500)"/>
                    </addColumn>
                    <dropColumn tableName="orders" columnName="legacy"/>
                    <createIndex tableName="orders" indexName="orders_slug_idx" unique="true">
                        <column name="slug"/>
                    </createIndex>
                    <addColumn tableName="unknown_table">
                        <column name="x" type="INT"/>
                    </addColumn>
                </changeSet>
                <include file="db/changelog/other.xml"/>
                """));

        assertThat(schema.tables()).hasSize(1);
        TableSchema orders = schema.tables().get(0);
        assertThat(orders.getColumns())
                .extracting(ColumnSchema::getName)
                .containsExactly("id", "code", "user_id", "slug", "comment");
        assertThat(orders.findColumn("id").isUnique()).isTrue();
        assertThat(orders.findColumn("id").isNotNull()).isTrue();
        assertThat(orders.findColumn("code").isUnique()).isTrue();
        assertThat(orders.findColumn("code").isNotNull()).isTrue();
        assertThat(orders.findColumn("user_id").getReference()).isEqualTo("users(id)");
        assertThat(orders.findColumn("slug").isUnique()).isTrue();
        assertThat(schema.warnings()).containsExactly("Included change log [db/changelog/other.xml] is not read");
    }

    @Test
    @DisplayName("Invalid XML is rejected")
    void fail_parse_invalid_xml() {
        assertThatThrownBy(() -> parser.parse("<databaseChangeLog><changeSet></databaseChangeLog>"))
                .isInstanceOf(SchemaConversionException.class)
                .hasMessageStartingWith("Schema is not a valid XML");
    }

    @Test
    @DisplayName("DOCTYPE declarations are forbidden to prevent reading of server files")
    void fail_parse_xml_with_doctype() {
        String xml =
                """
                <?xml version="1.0"?>
                <!DOCTYPE databaseChangeLog [<!ENTITY secret SYSTEM "file:///etc/passwd">]>
                <databaseChangeLog>
                    <changeSet id="1" author="a">
                        <createTable tableName="&secret;"><column name="id" type="INT"/></createTable>
                    </changeSet>
                </databaseChangeLog>
                """;

        assertThatThrownBy(() -> parser.parse(xml))
                .isInstanceOf(SchemaConversionException.class)
                .hasMessageStartingWith("Schema is not a valid XML");
    }

    @Test
    @DisplayName("Any broken change log is either parsed or rejected as invalid XML")
    void parse_broken_change_log() {
        String xml = changeLog(
                """
                <changeSet id="1" author="akvine">
                    <createTable tableName="t">
                        <column name="id" type="BIGINT"><constraints primaryKey="true"/></column>
                    </createTable>
                    <addUniqueConstraint tableName="t" columnNames="id, absent"/>
                    <createTable><column type="INT"/><column name="x"/></createTable>
                    <dropColumn tableName="t"/>
                </changeSet>
                """);

        assertThat(parser.parse(xml).tables()).extracting(TableSchema::getName).containsExactly("t");
        for (int length = 0; length < xml.length(); length++) {
            String prefix = xml.substring(0, length);
            assertThatThrownBy(() -> parser.parse(prefix)).isInstanceOf(SchemaConversionException.class);
        }
    }

    @Test
    @DisplayName("Deeply nested document doesn't overflow the stack")
    void parse_deeply_nested_document() {
        int depth = 50_000;
        String xml = "<a>".repeat(depth) + "<createTable tableName=\"t\"/>" + "</a>".repeat(depth);

        // Таблица лежит глубже, чем бывают изменения в журнале, поэтому не читается
        assertThat(parser.parse(xml).tables()).isEmpty();
    }

    private String changeLog(String body) {
        return CHANGE_LOG_START + body + CHANGE_LOG_END;
    }
}
