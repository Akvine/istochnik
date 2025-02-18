package ru.akvine.istochnik.services.generators.number.integer.configs;

import jakarta.annotation.Nullable;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.Config;
import ru.akvine.istochnik.services.generators.number.integer.IntegerShiftRange;

import java.util.Map;

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

    public boolean isNotNull() {
        return notNull;
    }

    public boolean isUnique() {
        return unique;
    }

    public RangeType getRangeType() {
        return rangeType;
    }

    public IntegerShiftRange getIntegerRange() {
        return integerShiftRange;
    }

    @Nullable
    public Map<String, Double> getFiltersWithValues() {
        return filtersWithValues;
    }

    public void setFiltersWithValues(@Nullable Map<String, Double> filtersWithValues) {
        this.filtersWithValues = filtersWithValues;
    }
}
