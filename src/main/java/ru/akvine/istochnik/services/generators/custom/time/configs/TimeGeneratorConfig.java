package ru.akvine.istochnik.services.generators.custom.time.configs;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.Config;
import ru.akvine.istochnik.services.generators.custom.time.TimeShiftRange;

import java.util.random.RandomGenerator;

@Data
@Accessors(chain = true)
public class TimeGeneratorConfig extends Config {
    private final boolean notNull;
    private final boolean unique;
    private final RangeType rangeType;
    private final TimeShiftRange timeShiftRange;

    public TimeGeneratorConfig(int size,
                               boolean notNull,
                               boolean unique,
                               RangeType rangeType,
                               TimeShiftRange timeShiftRange,
                               RandomGenerator randomGenerator) {
        super(size, randomGenerator);
        this.notNull = notNull;
        this.unique = unique;
        this.rangeType = rangeType;
        this.timeShiftRange = timeShiftRange;
    }
}
