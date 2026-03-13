package ru.akvine.istochnik.providers;

import java.util.Map;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.services.file.FileTableGenerator;

public record FileTableGeneratorsProvider(Map<FileType, FileTableGenerator> generators) {
    public FileTableGenerator getByType(FileType type) {
        if (generators.containsKey(type)) {
            return generators.get(type);
        }

        throw new UnsupportedOperationException(
                "File table generator not supported with type = [" + type + "] by app!");
    }
}
