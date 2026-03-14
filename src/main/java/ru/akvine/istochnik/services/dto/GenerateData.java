package ru.akvine.istochnik.services.dto;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.FileType;

@Data
@Accessors(chain = true)
public class GenerateData {
    private List<GenerateColumn> generateColumns;
    private int size;
    private FileType fileType;
}
