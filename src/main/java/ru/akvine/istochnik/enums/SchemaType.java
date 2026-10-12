package ru.akvine.istochnik.enums;

import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * Формат схемы, по которой строится запрос на генерацию
 */
@AllArgsConstructor
@Getter
public enum SchemaType {
    /**
     * SQL-скрипт с операторами CREATE TABLE
     */
    SQL("sql", ".sql"),
    /**
     * Журнал изменений Liquibase в формате XML
     */
    XML("xml", ".xml");

    private final String value;
    private final String extensionWithDot;

    public static SchemaType from(String value) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("Schema type can't be blank!");
        }

        for (SchemaType type : values()) {
            if (type.getValue().equalsIgnoreCase(value.trim())) {
                return type;
            }
        }

        throw new UnsupportedOperationException("Schema type = [" + value + "] is not supported by app");
    }

    /**
     * @param value    формат, заданный явно
     * @param fileName имя файла схемы: если формат не задан, он определяется по расширению файла
     */
    public static SchemaType resolve(String value, String fileName) {
        return StringUtils.isBlank(value) ? fromFileName(fileName) : from(value);
    }

    /**
     * Определяет формат схемы по расширению файла
     */
    public static SchemaType fromFileName(String fileName) {
        if (StringUtils.isNotBlank(fileName)) {
            String lowerCased = fileName.trim().toLowerCase(Locale.ROOT);
            for (SchemaType type : values()) {
                if (lowerCased.endsWith(type.getExtensionWithDot())) {
                    return type;
                }
            }
        }

        throw new UnsupportedOperationException(
                "Can't determine schema type by file name = [" + fileName + "]. Set parameter [type]");
    }
}
