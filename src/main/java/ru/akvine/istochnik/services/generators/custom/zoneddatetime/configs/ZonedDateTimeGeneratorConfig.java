package ru.akvine.istochnik.services.generators.custom.zoneddatetime.configs;

import java.util.random.RandomGenerator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.Config;
import ru.akvine.istochnik.services.generators.custom.zoneddatetime.ZonedDateTimeShiftRange;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ZonedDateTimeGeneratorConfig extends Config {
    private final boolean notNull;
    private final boolean unique;
    private final RangeType rangeType;
    private final ZonedDateTimeShiftRange zonedDateTimeShiftRange;

    public ZonedDateTimeGeneratorConfig(
            int size,
            boolean notNull,
            boolean unique,
            RangeType rangeType,
            ZonedDateTimeShiftRange zonedDateTimeShiftRange,
            RandomGenerator randomGenerator) {
        super(size, randomGenerator);
        this.notNull = notNull;
        this.unique = unique;
        this.rangeType = rangeType;
        this.zonedDateTimeShiftRange = zonedDateTimeShiftRange;
    }
}
