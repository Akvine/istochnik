package ru.akvine.istochnik.enums;

import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FileType {
    CSV("application/csv", "csv", ".csv"),
    XLSX("application/xlsx", "xlsx", ".xlsx"),
    SQL("application/sql", "sql", ".sql");

    private final String mimeType;
    private final String extension;
    private final String extensionWithDot;

    public static FileType from(String value) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("FileType can't be blank or null");
        }

        for (FileType type : values()) {
            if (type.toString().equalsIgnoreCase(value)) {
                return type;
            }
        }

        throw new UnsupportedOperationException("Target file type = [" + value + "] is not supported by app");
    }
}
