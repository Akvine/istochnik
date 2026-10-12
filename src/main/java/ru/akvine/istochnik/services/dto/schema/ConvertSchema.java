package ru.akvine.istochnik.services.dto.schema;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.SchemaType;

/**
 * Схема и параметры запросов на генерацию, которые нужно по ней построить
 */
@Data
@Accessors(chain = true)
public class ConvertSchema {
    private SchemaType type;
    private String content;

    /**
     * Количество строк в каждом запросе на генерацию
     */
    private int size;

    /**
     * Формат файла в каждом запросе на генерацию
     */
    private FileType fileType;
}
