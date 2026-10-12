package ru.akvine.istochnik.services.schema;

import ru.akvine.istochnik.enums.SchemaType;
import ru.akvine.istochnik.exceptions.SchemaConversionException;

/**
 * Читает из схемы таблицы, их колонки и ограничения
 */
public interface SchemaParser {
    /**
     * @throws SchemaConversionException если содержимое не является схемой этого формата
     */
    ParsedSchema parse(String content);

    SchemaType getType();
}
