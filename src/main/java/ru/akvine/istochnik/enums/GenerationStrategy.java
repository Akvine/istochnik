package ru.akvine.istochnik.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;

@Getter
@AllArgsConstructor
public enum GenerationStrategy {
    CONSTANT("constant"),
    ALGORITHM("algorithm");

    private final String name;

    public static GenerationStrategy from(String value) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("Generation strategy can't be blank!");
        }

        for (GenerationStrategy strategy : values()) {
            if (strategy.getName().equalsIgnoreCase(value)) {
                return strategy;
            }
        }

        throw new UnsupportedTypeGenerationException("Generation strategy = [" + value + "] is not supported by app!");
    }
}
