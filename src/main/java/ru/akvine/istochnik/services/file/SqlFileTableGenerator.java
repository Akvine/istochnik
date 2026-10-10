package ru.akvine.istochnik.services.file;

import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.services.dto.Table;

@Service
public class SqlFileTableGenerator implements FileTableGenerator {

    @Value("${sql.insert.batch.size:1000}")
    private int insertBatchSize;

    @Override
    public byte[] generate(Table table) {
        int rowsCount = table.getRowsCount();
        int batchSize = insertBatchSize > 0 ? insertBatchSize : Math.max(rowsCount, 1);

        String insertHeader = "INSERT INTO " + table.getTableName() + " (" + extractColumnNames(table) + ") VALUES\n";

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
                    sql.append(formatValue(value)).append(")");
                } else {
                    sql.append(formatValue(value)).append(", ");
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

    private String extractColumnNames(Table table) {
        StringBuilder sb = new StringBuilder();

        int lastColumnIndex = table.getColumns().size() - 1;
        for (int i = 0; i < table.getColumns().size(); ++i) {
            if (i == lastColumnIndex) {
                sb.append(table.getColumns().get(i).getName().toUpperCase());
            } else {
                sb.append(table.getColumns().get(i).getName().toUpperCase()).append(", ");
            }
        }

        return sb.toString();
    }

    private static String formatValue(Object value) {
        switch (value) {
            case null -> {
                return "NULL";
            }
            case String s -> {
                String escaped = s.replace("'", "''");
                return "'" + escaped + "'";
            }
            case Number ignored -> {
                return value.toString();
            }
            case Boolean b -> {
                return b ? "TRUE" : "FALSE";
            }
            default -> {}
        }

        throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
    }
}
