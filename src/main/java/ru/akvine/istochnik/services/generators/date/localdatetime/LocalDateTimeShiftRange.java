package ru.akvine.istochnik.services.generators.date.localdatetime;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.DateShiftType;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class LocalDateTimeShiftRange {
    private LocalDateTime start;
    private LocalDateTime end;
    private DateShiftType dateShiftType;
    private long shiftCount;
}
