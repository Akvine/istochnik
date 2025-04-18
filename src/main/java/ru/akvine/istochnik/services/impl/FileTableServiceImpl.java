package ru.akvine.istochnik.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.managers.FileTableGeneratorsManager;
import ru.akvine.istochnik.services.FileTableService;
import ru.akvine.istochnik.services.dto.Table;

@Service
@RequiredArgsConstructor
public class FileTableServiceImpl implements FileTableService {
    private final FileTableGeneratorsManager fileTableGeneratorsManager;

    @Override
    public byte[] generateFile(FileType type, Table table) {
        Asserts.isNotNull(table);
        return fileTableGeneratorsManager.getByType(type).generate(table);
    }
}
