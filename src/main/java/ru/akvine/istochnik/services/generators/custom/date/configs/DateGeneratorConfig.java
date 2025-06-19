package ru.akvine.istochnik.services.generators.custom.date.configs;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.Config;
import ru.akvine.istochnik.services.generators.custom.date.DateShiftRange;

import java.util.random.RandomGenerator;

@Data
@Accessors(chain = true)
public class DateGeneratorConfig extends Config {
    private final boolean notNull;
    private final boolean unique;
    private final RangeType rangeType;
    private final DateShiftRange dateShiftRange;

    public DateGeneratorConfig(int size,
                               boolean notNull,
                               boolean unique,
                               RangeType rangeType,
                               DateShiftRange dateShiftRange,
                               RandomGenerator randomGenerator) {
        super(size, randomGenerator);
        this.notNull = notNull;
        this.unique = unique;
        this.rangeType = rangeType;
        this.dateShiftRange = dateShiftRange;
    }
}
