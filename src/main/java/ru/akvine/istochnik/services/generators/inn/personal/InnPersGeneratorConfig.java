package ru.akvine.istochnik.services.generators.inn.personal;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.services.generators.Config;

@Data
@Accessors(chain = true)
public class InnPersGeneratorConfig extends Config {
    private final boolean notNull;
    private final boolean unique;
    private final boolean valid;

    public InnPersGeneratorConfig(int size, boolean notNull, boolean unique, boolean valid) {
        super(size);
        this.notNull = notNull;
        this.unique = unique;
        this.valid = valid;
    }
}
