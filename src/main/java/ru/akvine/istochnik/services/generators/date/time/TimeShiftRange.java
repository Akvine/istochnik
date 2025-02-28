package ru.akvine.istochnik.services.generators.date.time;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.DateShiftType;

import java.time.LocalTime;

@Data
@Accessors(chain = true)
public class TimeShiftRange {
    private LocalTime start;
    private LocalTime end;
    private DateShiftType dateShiftType;
    private int shiftCount;
}
