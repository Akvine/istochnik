package ru.akvine.istochnik.services.generators.custom.imei;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.services.generators.Config;

import java.util.random.RandomGenerator;

@Data
@Accessors(chain = true)
public class ImeiGeneratorConfig extends Config {
    private final boolean notNull;
    private final boolean unique;
    private final boolean valid;

    public ImeiGeneratorConfig(int size,
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
