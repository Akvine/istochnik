package ru.akvine.istochnik.services;

import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.services.dto.Table;

public interface FileTableService {
    byte[] generateFile(FileType type, Table table);
}
