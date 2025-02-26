package ru.akvine.istochnik.services.generators.number.integer.configs;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.generators.Config;
import ru.akvine.istochnik.services.generators.number.integer.IntegerShiftRange;

import java.util.List;

@Data
@Accessors(chain = true)
public class IntegerGeneratorConfig extends Config {
    private final boolean notNull;
    private final boolean unique;
    private final RangeType rangeType;
    private final IntegerShiftRange integerShiftRange;
    private final List<Filter> filters;

    public IntegerGeneratorConfig(int size,
                                  boolean notNull,
                                  boolean unique,
                                  RangeType rangeType,
                                  IntegerShiftRange integerShiftRange,
                                  List<Filter> filters) {
        super(size);
        this.notNull = notNull;
        this.unique = unique;
        this.rangeType = rangeType;
        this.integerShiftRange = integerShiftRange;
        this.filters = filters;
    }
}
