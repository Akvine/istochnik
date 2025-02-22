package ru.akvine.istochnik.services.generators.number.integer;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class IntegerShiftRange {
    private Integer start;
    private int end;
    private int step;
}
