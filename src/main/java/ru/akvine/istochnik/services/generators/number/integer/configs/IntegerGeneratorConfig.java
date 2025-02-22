package ru.akvine.istochnik.services.generators.number.integer.configs;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.Config;
import ru.akvine.istochnik.services.generators.number.integer.IntegerShiftRange;

import java.util.Map;

@Data
@Accessors(chain = true)
public class IntegerGeneratorConfig extends Config {
    private final boolean notNull;
    private final boolean unique;
    private final RangeType rangeType;
    private final IntegerShiftRange integerShiftRange;

    @Nullable
    private Map<String, Double> filtersWithValues;

    public IntegerGeneratorConfig(int size,
                                  boolean notNull,
                                  boolean unique,
                                  RangeType rangeType,
                                  IntegerShiftRange integerShiftRange) {
        super(size);
        this.notNull = notNull;
        this.unique = unique;
        this.rangeType = rangeType;
        this.integerShiftRange = integerShiftRange;
    }
}
