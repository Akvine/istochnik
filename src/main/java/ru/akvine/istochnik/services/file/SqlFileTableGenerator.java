package ru.akvine.istochnik.services.file;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.services.dto.Table;

import java.nio.charset.StandardCharsets;

@Service
public class SqlFileTableGenerator implements FileTableGenerator {

    @Override
    public byte[] generate(Table table) {
        int rowsCount = table.getRowsCount();

        String columnNames = extractColumnNames(table);

        StringBuilder sql = new StringBuilder();
        sql
                .append("INSERT INTO ")
                .append(" (")
                .append(columnNames)
                .append(") VALUES\n");


        // Перебираем строки
        for (int row = 0; row < rowsCount; row++) {
            if (row > 0) sql.append(",\n");
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
        }
        sql.append(";\n");

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
            default -> {
            }
        }

        throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
    }
}
