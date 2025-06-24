package ru.akvine.istochnik.services.generators.base.number.integer;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class IntegerShiftRange {
    private Long start;
    private Long end;
    private Long step;
}
