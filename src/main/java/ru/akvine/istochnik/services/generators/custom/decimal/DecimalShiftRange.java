package ru.akvine.istochnik.services.generators.custom.decimal;

import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DecimalShiftRange {
    private BigDecimal start;

    @Nullable
    private BigDecimal end;

    private BigDecimal step;
}
