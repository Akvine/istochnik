package ru.akvine.istochnik.services.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.util.CollectionUtils;
import ru.akvine.istochnik.exceptions.AddColumnException;

import java.util.List;

@Data
@Accessors(chain = true)
public class Table {
    private final int rowsCount;

    private List<List<Object>> values;

    public Table(int rowsCount) {
        this.rowsCount = rowsCount;
    }

    public void addColumn(List<Object> column) {
        if (CollectionUtils.isEmpty(column)) {
            throw new AddColumnException("Column is empty or null");
        }

        if (column.size() != rowsCount) {
            String errorMessage = String.format(
                    "Column size = [%s] is not equal to init rows count = [%s]",
                    column.size(), rowsCount
            );
            throw new AddColumnException(errorMessage);
        }

        values.add(column);
    }

    public int getColumnsCount() {
        return values.size();
    }
}
