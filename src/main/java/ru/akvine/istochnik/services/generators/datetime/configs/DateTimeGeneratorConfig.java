package ru.akvine.istochnik.services.generators.datetime.configs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.Config;
import ru.akvine.istochnik.services.generators.datetime.DateTimeShiftRange;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class DateTimeGeneratorConfig extends Config {
    private final boolean notNull;
    private final boolean unique;
    private final RangeType rangeType;
    private final DateTimeShiftRange dateTimeShiftRange;

    public DateTimeGeneratorConfig(int size,
                                   boolean notNull,
                                   boolean unique,
                                   RangeType rangeType,
                                   DateTimeShiftRange dateTimeShiftRange) {
        super(size);
        this.notNull = notNull;
        this.unique = unique;
        this.rangeType = rangeType;
        this.dateTimeShiftRange = dateTimeShiftRange;
    }
}
