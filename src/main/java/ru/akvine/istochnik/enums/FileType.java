package ru.akvine.istochnik.enums;

import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FileType {
    CSV;

    public static FileType from(String value) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("FileType can't be blank or null");
        }

        switch (value.toLowerCase()) {
            case "csv" -> {
                return CSV;
            }
            default -> throw new UnsupportedOperationException("FileType = [" + value + "] is not supported!");
        }
    }
}
