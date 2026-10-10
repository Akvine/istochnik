package ru.akvine.istochnik.services.generators.custom.requisite;

import java.util.random.RandomGenerator;
import lombok.Getter;
import ru.akvine.istochnik.services.generators.Config;

@Getter
public class RequisiteGeneratorConfig extends Config {
    private final boolean notNull;
    private final boolean unique;
    private final boolean valid;

    public RequisiteGeneratorConfig(
            int size, boolean notNull, boolean unique, boolean valid, RandomGenerator randomGenerator) {
        super(size, randomGenerator);
        this.notNull = notNull;
        this.unique = unique;
        this.valid = valid;
    }
}
