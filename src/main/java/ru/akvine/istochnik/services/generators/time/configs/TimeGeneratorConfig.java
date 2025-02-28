package ru.akvine.istochnik.services.generators.time.configs;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.Config;
import ru.akvine.istochnik.services.generators.time.TimeShiftRange;

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
                               TimeShiftRange timeShiftRange) {
        super(size);
        this.notNull = notNull;
        this.unique = unique;
        this.rangeType = rangeType;
        this.timeShiftRange = timeShiftRange;
    }
}
