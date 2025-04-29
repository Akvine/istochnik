package ru.akvine.istochnik.services.generators.base.bool.configs;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.Config;
import ru.akvine.istochnik.services.generators.base.bool.BooleanShiftRange;

@Data
@Accessors(chain = true)
public class BooleanGeneratorConfig extends Config {
    private final boolean notNull;
    private final boolean unique;
    private final RangeType rangeType;
    private final BooleanShiftRange booleanShiftRange;

    public BooleanGeneratorConfig(int size,
                                  boolean notNull,
                                  boolean unique,
                                  RangeType rangeType,
                                  BooleanShiftRange booleanShiftRange) {
        super(size);
        this.notNull = notNull;
        this.unique = unique;
        this.rangeType = rangeType;
        this.booleanShiftRange = booleanShiftRange;
    }
}
