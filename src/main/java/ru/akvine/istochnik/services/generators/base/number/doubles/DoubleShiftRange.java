package ru.akvine.istochnik.services.generators.base.number.doubles;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DoubleShiftRange {
    private Double start;
    private double end;
    private Double step;
}
