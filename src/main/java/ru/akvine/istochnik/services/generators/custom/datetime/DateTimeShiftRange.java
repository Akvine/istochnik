package ru.akvine.istochnik.services.generators.custom.datetime;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.DateShiftType;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class DateTimeShiftRange {
    private LocalDateTime start;
    private LocalDateTime end;
    private DateShiftType dateShiftType;
    private long shiftCount;
}
