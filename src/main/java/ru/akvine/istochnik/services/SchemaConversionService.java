package ru.akvine.istochnik.services;

import ru.akvine.istochnik.exceptions.SchemaConversionDisabledException;
import ru.akvine.istochnik.exceptions.SchemaConversionException;
import ru.akvine.istochnik.services.dto.schema.ConvertSchema;
import ru.akvine.istochnik.services.dto.schema.ConvertedSchema;

/**
 * Конвертация схем: по описанию таблиц в SQL-скрипте или журнале изменений Liquibase строятся
 * запросы на генерацию данных для этих таблиц
 */
public interface SchemaConversionService {
    /**
     * Включена ли конвертация схем в настройках приложения
     */
    boolean isEnabled();

    /**
     * @throws SchemaConversionDisabledException если конвертация отключена в настройках
     * @throws SchemaConversionException         если схема не разобрана или в ней нет таблиц
     */
    ConvertedSchema convert(ConvertSchema convertSchema);
}
