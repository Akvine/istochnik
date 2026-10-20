package ru.akvine.istochnik.services.dto;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.SqlDialect;

@Data
@Accessors(chain = true)
public class GenerateData {
    private List<GenerateColumn> generateColumns;
    private int size;
    private FileType fileType;
    private String tableName;
    private SqlDialect sqlDialect;
}
