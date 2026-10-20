package ru.akvine.istochnik.controllers.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class GenerateTableRequest {
    private List<ColumnDto> columns;
    private int size;
    private String fileType;
    private String tableName;

    /**
     * Диалект SQL для файла в формате SQL: postgresql, mysql, mssql, oracle или sqlite
     */
    private String sqlDialect;

    /**
     * Seed всего запроса: каждая колонка без своего seed получает из него свой seed по имени колонки
     */
    private String seed;
}
