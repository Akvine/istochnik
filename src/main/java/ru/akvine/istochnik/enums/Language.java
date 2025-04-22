package ru.akvine.istochnik.enums;

import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Language {
    RU("ru"),
    EN("en");

    private final String value;

    public static Language from(String value) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("Value for " + Language.class.getSimpleName() + " can't be blank!");
        }

        for (Language language : values()) {
            if (language.getValue().equalsIgnoreCase(value)) {
                return language;
            }
        }

        throw new IllegalArgumentException("Language = [" + value + "] is not supported by app!");
    }
}
