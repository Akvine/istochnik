package ru.akvine.istochnik.managers;

import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.services.file.FileTableGenerator;

import java.util.Map;

public record FileTableGeneratorsManager(Map<FileType, FileTableGenerator> generators) {
    public FileTableGenerator getByType(FileType type) {
        if (generators.containsKey(type)) {
            return generators.get(type);
        }

        throw new UnsupportedOperationException("File table generator not supported with type = [" + type + "] by app!");
    }
}
