package ru.akvine.istochnik.services.generators.date.localdatetime.configs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.Config;
import ru.akvine.istochnik.services.generators.date.localdatetime.LocalDateTimeShiftRange;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class LocalDateTimeGeneratorConfig extends Config {
    private final boolean notNull;
    private final boolean unique;
    private final RangeType rangeType;
    private final LocalDateTimeShiftRange localDateTimeShiftRange;

    public LocalDateTimeGeneratorConfig(int size,
                                        boolean notNull,
                                        boolean unique,
                                        RangeType rangeType,
                                        LocalDateTimeShiftRange localDateTimeShiftRange) {
        super(size);
        this.notNull = notNull;
        this.unique = unique;
        this.rangeType = rangeType;
        this.localDateTimeShiftRange = localDateTimeShiftRange;
    }
}
