package ru.akvine.istochnik.services.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.SqlDialect;
import ru.akvine.istochnik.exceptions.AddColumnException;

@Data
@Accessors(chain = true)
public class Table {
    private String tableName;

    /**
     * Диалект SQL для выгрузки в формате SQL. Если не задан, файл получается таким же, как до появления диалектов
     */
    private SqlDialect sqlDialect;

    private final int rowsCount;
    private List<Column> columns;

    public Table(int rowsCount, String tableName) {
        this.rowsCount = rowsCount;
        this.tableName = tableName;
        columns = new ArrayList<>();
    }

    public void addColumn(String columnName, List<?> values) {
        if (values == null) {
            throw new AddColumnException("Column is null");
        }

        if (values.size() != rowsCount) {
            String errorMessage = String.format(
                    "Column size = [%s] is not equal to init rows count = [%s]", values.size(), rowsCount);
            throw new AddColumnException(errorMessage);
        }

        columns.add(new Column().setName(columnName).setValues(values));
    }

    public int getColumnsCount() {
        return columns.size();
    }
}
