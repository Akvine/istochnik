package ru.akvine.istochnik.services.generators.base.bool;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BooleanShiftRange {
    private boolean start;
}
