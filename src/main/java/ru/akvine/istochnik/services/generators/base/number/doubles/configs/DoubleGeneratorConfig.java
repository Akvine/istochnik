package ru.akvine.istochnik.services.generators.base.number.doubles.configs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.Config;
import ru.akvine.istochnik.services.generators.base.number.doubles.DoubleShiftRange;

import java.util.random.RandomGenerator;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class DoubleGeneratorConfig extends Config {
    private final boolean notNull;
    private final boolean unique;
    private final RangeType rangeType;
    private final DoubleShiftRange doubleShiftRange;

    public DoubleGeneratorConfig(int size,
                                 boolean notNull,
                                 boolean unique,
                                 RangeType rangeType,
                                 DoubleShiftRange doubleShiftRange,
                                 RandomGenerator randomGenerator) {
        super(size, randomGenerator);
        this.notNull = notNull;
        this.unique = unique;
        this.rangeType = rangeType;
        this.doubleShiftRange = doubleShiftRange;
    }
}
