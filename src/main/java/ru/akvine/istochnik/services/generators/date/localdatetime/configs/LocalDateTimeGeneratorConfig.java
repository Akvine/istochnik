package ru.akvine.istochnik.services.generators.date.localdatetime.configs;

import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.Config;
import ru.akvine.istochnik.services.generators.date.localdatetime.LocalDateTimeShiftRange;

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

    public boolean isNotNull() {
        return notNull;
    }

    public boolean isUnique() {
        return unique;
    }

    public RangeType getRangeType() {
        return rangeType;
    }

    public LocalDateTimeShiftRange getLocalDateTimeShiftRange() {
        return localDateTimeShiftRange;
    }
}
