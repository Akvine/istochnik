package ru.akvine.istochnik.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;

@Getter
@AllArgsConstructor
public enum RangeType {
    SHIFT("shift"),
    RANDOM("random");

    private final String type;

    public static RangeType from(String type) {
        if (StringUtils.isBlank(type)) {
            throw new IllegalArgumentException("Range type can't be blank!");
        }

        for (RangeType rangeType : values()) {
            if (rangeType.getType().equalsIgnoreCase(type)) {
                return rangeType;
            }
        }

        throw new UnsupportedTypeGenerationException("Range type = [" + type + "] is not supported by app!");
    }
}
