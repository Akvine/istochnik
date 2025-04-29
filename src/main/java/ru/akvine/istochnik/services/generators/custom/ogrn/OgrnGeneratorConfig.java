package ru.akvine.istochnik.services.generators.custom.ogrn;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.services.generators.Config;

@Data
@Accessors(chain = true)
public class OgrnGeneratorConfig extends Config {
    private final boolean notNull;
    private final boolean unique;
    private final boolean valid;

    public OgrnGeneratorConfig(int size, boolean notNull, boolean unique, boolean valid) {
        super(size);
        this.notNull = notNull;
        this.unique = unique;
        this.valid = valid;
    }
}
