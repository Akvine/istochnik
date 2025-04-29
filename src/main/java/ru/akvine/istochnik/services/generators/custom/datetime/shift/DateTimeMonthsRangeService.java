package ru.akvine.istochnik.services.generators.custom.datetime.shift;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.DateShiftType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DateTimeMonthsRangeService extends AbstractDateTimeRangeService<LocalDateTime, Long> {
    @Override
    public List<LocalDateTime> range(LocalDateTime start, LocalDateTime end, Long step) {
        List<LocalDateTime> range = new ArrayList<>();
        for (LocalDateTime date = start; date.isBefore(end) || date.isEqual(end); date = date.plusMonths(step)) {
            range.add(date);
        }
        return range;
    }

    @Override
    public DateShiftType getByType() {
        return DateShiftType.MONTH;
    }
}
