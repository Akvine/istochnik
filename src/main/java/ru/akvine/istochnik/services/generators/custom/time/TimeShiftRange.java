package ru.akvine.istochnik.services.generators.custom.time;

import java.time.LocalTime;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.DateShiftType;

@Data
@Accessors(chain = true)
public class TimeShiftRange {
    private LocalTime start;
    private LocalTime end;
    private DateShiftType dateShiftType;
    private int shiftCount;
}
