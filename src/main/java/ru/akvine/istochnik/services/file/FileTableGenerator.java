package ru.akvine.istochnik.services.file;

import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.services.dto.Table;

public interface FileTableGenerator {
    byte[] generate(Table table);

    FileType getType();
}
