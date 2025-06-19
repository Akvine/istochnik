package ru.akvine.istochnik.services.generators.custom.snils.configs;

import lombok.Getter;
import ru.akvine.istochnik.services.generators.Config;

import java.util.random.RandomGenerator;

@Getter
public class SnilsGeneratorConfig extends Config {
    private final boolean notNull;
    private final boolean unique;
    private final boolean valid;

    public SnilsGeneratorConfig(int size,
                                boolean notNull,
                                boolean unique,
                                boolean valid,
                                RandomGenerator randomGenerator) {
        super(size, randomGenerator);
        this.notNull = notNull;
        this.unique = unique;
        this.valid = valid;
    }
}
