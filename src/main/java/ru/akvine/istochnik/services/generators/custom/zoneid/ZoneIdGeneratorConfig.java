package ru.akvine.istochnik.services.generators.custom.zoneid;

import java.util.random.RandomGenerator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.Config;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ZoneIdGeneratorConfig extends Config {
    private final boolean notNull;
    private final boolean unique;
    private final RangeType rangeType;

    public ZoneIdGeneratorConfig(
            int size, boolean notNull, boolean unique, RangeType rangeType, RandomGenerator randomGenerator) {
        super(size, randomGenerator);
        this.notNull = notNull;
        this.unique = unique;
        this.rangeType = rangeType;
    }
}
