package ru.akvine.istochnik.services.generators.custom.decimal.configs;

import java.util.random.RandomGenerator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.Config;
import ru.akvine.istochnik.services.generators.custom.decimal.DecimalShiftRange;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class DecimalGeneratorConfig extends Config {
    private final boolean notNull;
    private final boolean unique;
    private final RangeType rangeType;
    private final DecimalShiftRange decimalShiftRange;

    /**
     * Количество знаков после запятой у генерируемых значений
     */
    private final int scale;

    public DecimalGeneratorConfig(
            int size,
            boolean notNull,
            boolean unique,
            RangeType rangeType,
            DecimalShiftRange decimalShiftRange,
            int scale,
            RandomGenerator randomGenerator) {
        super(size, randomGenerator);
        this.notNull = notNull;
        this.unique = unique;
        this.rangeType = rangeType;
        this.decimalShiftRange = decimalShiftRange;
        this.scale = scale;
    }
}
