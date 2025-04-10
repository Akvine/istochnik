package ru.akvine.istochnik.services.generators.snils.configs;

import lombok.Getter;
import ru.akvine.istochnik.services.generators.Config;

@Getter
public class SnilsGeneratorConfig extends Config {
    private final boolean notNull;
    private final boolean unique;
    private final boolean valid;

    public SnilsGeneratorConfig(int size, boolean notNull, boolean unique, boolean valid) {
        super(size);
        this.notNull = notNull;
        this.unique = unique;
        this.valid = valid;
    }
}
