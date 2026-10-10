package ru.akvine.istochnik.services.file;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.akvine.istochnik.services.dto.Table;

public class SqlFileTableGeneratorTest {

    @Test
    @DisplayName("Rows are split into several INSERT statements by batch size")
    void rows_are_split_into_batches() {
        String sql = generate(2, table(5));

        assertThat(sql)
                .isEqualTo("INSERT INTO users (ID, NAME) VALUES\n"
                        + "(1, 'name1'),\n"
                        + "(2, 'name2');\n"
                        + "INSERT INTO users (ID, NAME) VALUES\n"
                        + "(3, 'name3'),\n"
                        + "(4, 'name4');\n"
                        + "INSERT INTO users (ID, NAME) VALUES\n"
                        + "(5, 'name5');\n");
    }

    @Test
    @DisplayName("Rows count is multiple of batch size - no empty INSERT statement at the end")
    void rows_count_is_multiple_of_batch_size() {
        String sql = generate(2, table(4));

        assertThat(sql)
                .isEqualTo("INSERT INTO users (ID, NAME) VALUES\n"
                        + "(1, 'name1'),\n"
                        + "(2, 'name2');\n"
                        + "INSERT INTO users (ID, NAME) VALUES\n"
                        + "(3, 'name3'),\n"
                        + "(4, 'name4');\n");
    }

    @Test
    @DisplayName("Batch size is greater than rows count - single INSERT statement")
    void batch_size_is_greater_than_rows_count() {
        String sql = generate(1000, table(3));

        assertThat(sql)
                .isEqualTo("INSERT INTO users (ID, NAME) VALUES\n"
                        + "(1, 'name1'),\n"
                        + "(2, 'name2'),\n"
                        + "(3, 'name3');\n");
    }

    @Test
    @DisplayName("Batch size <= 0 disables splitting")
    void non_positive_batch_size_disables_splitting() {
        String expected = "INSERT INTO users (ID, NAME) VALUES\n"
                + "(1, 'name1'),\n"
                + "(2, 'name2'),\n"
                + "(3, 'name3');\n";

        assertThat(generate(0, table(3))).isEqualTo(expected);
        assertThat(generate(-1, table(3))).isEqualTo(expected);
    }

    @Test
    @DisplayName("No rows - no INSERT statements")
    void no_rows_no_statements() {
        assertThat(generate(2, table(0))).isEmpty();
    }

    @Test
    @DisplayName("Null values and quotes are formatted in every batch")
    void null_values_and_quotes_are_formatted() {
        Table table = new Table(2, "users");
        table.addColumn("name", Arrays.asList("O'Neil", null));

        assertThat(generate(1, table))
                .isEqualTo("INSERT INTO users (NAME) VALUES\n"
                        + "('O''Neil');\n"
                        + "INSERT INTO users (NAME) VALUES\n"
                        + "(NULL);\n");
    }

    private String generate(int batchSize, Table table) {
        SqlFileTableGenerator generator = new SqlFileTableGenerator();
        ReflectionTestUtils.setField(generator, "insertBatchSize", batchSize);
        return new String(generator.generate(table), StandardCharsets.UTF_8);
    }

    private Table table(int rowsCount) {
        List<Long> ids = LongStream.rangeClosed(1, rowsCount).boxed().toList();
        List<String> names = ids.stream().map(id -> "name" + id).toList();

        Table table = new Table(rowsCount, "users");
        table.addColumn("id", ids);
        table.addColumn("name", names);
        return table;
    }
}
