package ru.akvine.istochnik.services.generators.custom.time.configs;

import java.util.random.RandomGenerator;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.distribution.Distribution;
import ru.akvine.istochnik.services.distribution.UniformDistribution;
import ru.akvine.istochnik.services.generators.Config;
import ru.akvine.istochnik.services.generators.custom.time.TimeShiftRange;

@Data
@Accessors(chain = true)
public class TimeGeneratorConfig extends Config {
    private final boolean notNull;
    private final boolean unique;
    private final RangeType rangeType;
    private final TimeShiftRange timeShiftRange;
    private Distribution distribution = UniformDistribution.INSTANCE;

    public TimeGeneratorConfig(
            int size,
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
