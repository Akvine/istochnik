package ru.akvine.istochnik.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;

@Getter
@AllArgsConstructor
public enum GenerationStrategy {
    CONSTANT("constant", "constant.strategy.description", false, false, false),
    ALGORITHM("algorithm", "algorithm.strategy.description", true, true, true),
    DICTIONARY("dictionary", "dictionary.strategy.description", true, true, false),
    REGEXP("regexp", "regexp.strategy.description", false, true, false),
    FAKER("faker", "faker.strategy.description", false, true, false);

    private final String name;
    private final String code;
    private final boolean supportsUnique;
    private final boolean supportsNotNull;
    private final boolean supportsValid;

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
