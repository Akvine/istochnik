package ru.akvine.istochnik.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.providers.FileTableGeneratorsProvider;
import ru.akvine.istochnik.services.FileTableService;
import ru.akvine.istochnik.services.dto.Table;

@Service
@RequiredArgsConstructor
public class FileTableServiceImpl implements FileTableService {
    private final FileTableGeneratorsProvider fileTableGeneratorsProvider;

    @Override
    public byte[] generateFile(FileType type, Table table) {
        Asserts.isNotNull(table);
        return fileTableGeneratorsProvider.getByType(type).generate(table);
    }
}
