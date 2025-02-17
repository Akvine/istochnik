package ru.akvine.istochnik.services.generators.date.localdatetime;

import ru.akvine.istochnik.enums.DateShiftType;

import java.time.LocalDateTime;

public class LocalDateTimeShiftRange {
    private LocalDateTime start;

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public DateShiftType getDateShiftType() {
        return dateShiftType;
    }

    public void setDateShiftType(DateShiftType dateShiftType) {
        this.dateShiftType = dateShiftType;
    }

    public long getShiftCount() {
        return shiftCount;
    }

    public void setShiftCount(long shiftCount) {
        this.shiftCount = shiftCount;
    }

    private LocalDateTime end;
    private DateShiftType dateShiftType;
    private long shiftCount;
}
