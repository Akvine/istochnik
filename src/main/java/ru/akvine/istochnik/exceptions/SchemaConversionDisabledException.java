package ru.akvine.istochnik.exceptions;

/**
 * Конвертация схем отключена в настройках приложения
 */
public class SchemaConversionDisabledException extends RuntimeException {
    public SchemaConversionDisabledException() {
        super("Schema conversion is disabled by app settings");
    }
}
