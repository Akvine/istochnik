package ru.akvine.istochnik.exceptions;

/**
 * По схеме нельзя построить запрос на генерацию: неверные параметры, схема не разобрана или в ней нет таблиц
 */
public class SchemaConversionException extends RuntimeException {
    public SchemaConversionException(String message) {
        super(message);
    }

    public SchemaConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
