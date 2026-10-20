package ru.akvine.istochnik.services.file;

import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.SqlDialect;
import ru.akvine.istochnik.services.dto.Table;

@Service
public class SqlFileTableGenerator implements FileTableGenerator {

    @Value("${sql.insert.batch.size:1000}")
    private int insertBatchSize;

    @Override
    public byte[] generate(Table table) {
        // Без диалекта файл получается таким же, как до появления диалектов
        SqlDialect dialect = table.getSqlDialect();
        int rowsCount = table.getRowsCount();
        int batchSize = insertBatchSize > 0 ? insertBatchSize : Math.max(rowsCount, 1);
        // Диалект может ограничивать число строк в одном INSERT сильнее настройки
        if (dialect != null && dialect.getMaxRowsPerInsert() > 0) {
            batchSize = Math.min(batchSize, dialect.getMaxRowsPerInsert());
        }

        String tableName = dialect == null ? table.getTableName() : dialect.quoteTableName(table.getTableName());
        String insertHeader = "INSERT INTO " + tableName + " (" + extractColumnNames(table, dialect) + ") VALUES\n";

        StringBuilder sql = new StringBuilder();

        // Перебираем строки
        for (int row = 0; row < rowsCount; row++) {
            if (row % batchSize == 0) {
                sql.append(insertHeader);
            }
            sql.append("(");

            int lastColumnIndex = table.getColumns().size() - 1;
            for (int columnIndex = 0; columnIndex < table.getColumns().size(); columnIndex++) {
                Object value = table.getColumns().get(columnIndex).getValues().get(row);
                if (columnIndex == lastColumnIndex) {
                    sql.append(formatValue(value, dialect)).append(")");
                } else {
                    sql.append(formatValue(value, dialect)).append(", ");
                }
            }

            boolean lastInBatch = (row + 1) % batchSize == 0 || row == rowsCount - 1;
            sql.append(lastInBatch ? ";\n" : ",\n");
        }

        return sql.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public FileType getType() {
        return FileType.SQL;
    }

    private String extractColumnNames(Table table, SqlDialect dialect) {
        StringBuilder sb = new StringBuilder();

        int lastColumnIndex = table.getColumns().size() - 1;
        for (int i = 0; i < table.getColumns().size(); ++i) {
            if (i == lastColumnIndex) {
                sb.append(columnName(table.getColumns().get(i).getName(), dialect));
            } else {
                sb.append(columnName(table.getColumns().get(i).getName(), dialect)).append(", ");
            }
        }

        return sb.toString();
    }

    private String columnName(String name, SqlDialect dialect) {
        return dialect == null ? name.toUpperCase() : dialect.quoteName(name);
    }

    private static String formatValue(Object value, SqlDialect dialect) {
        switch (value) {
            case null -> {
                return "NULL";
            }
            case String s -> {
                if (dialect != null) {
                    return dialect.quoteString(s);
                }
                String escaped = s.replace("'", "''");
                return "'" + escaped + "'";
            }
            case Number ignored -> {
                return value.toString();
            }
            case Boolean b -> {
                return dialect == null ? (b ? "TRUE" : "FALSE") : dialect.booleanLiteral(b);
            }
            default -> {}
        }

        throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
    }
}
