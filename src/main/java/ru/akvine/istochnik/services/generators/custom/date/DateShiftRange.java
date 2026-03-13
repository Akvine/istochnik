package ru.akvine.istochnik.services.generators.custom.date;

import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.DateShiftType;

@Data
@Accessors(chain = true)
public class DateShiftRange {
    private LocalDate start;
    private LocalDate end;
    private DateShiftType dateShiftType;
    private long shiftCount;
}
