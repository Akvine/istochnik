package ru.akvine.istochnik.services.generators.base.number.integer;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class IntegerShiftRange {
    private Integer start;
    private int end;
    private Integer step;
}
