package ru.akvine.istochnik.services.generators.custom.date;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.DateShiftType;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class DateShiftRange {
    private LocalDate start;
    private LocalDate end;
    private DateShiftType dateShiftType;
    private long shiftCount;
}
