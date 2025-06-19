package ru.akvine.istochnik.services.generators.custom.ogrn;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.services.generators.Config;

import java.util.random.RandomGenerator;

@Data
@Accessors(chain = true)
public class OgrnGeneratorConfig extends Config {
    private final boolean notNull;
    private final boolean unique;
    private final boolean valid;

    public OgrnGeneratorConfig(int size,
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
