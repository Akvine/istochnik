package ru.akvine.istochnik.services.generators.date.localdatetime.shift;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.DateShiftType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class LocalDateTimeYearsShiftService extends AbstractLocalDateTimeShiftService<LocalDateTime, Long> {
    @Override
    public Collection<LocalDateTime> shift(LocalDateTime start, LocalDateTime end, Long step) {
        List<LocalDateTime> range = new ArrayList<>();
        for (LocalDateTime date = start; date.isBefore(end) || date.isEqual(end); date = date.plusDays(step)) {
            range.add(date);
        }
        return range;
    }

    @Override
    public DateShiftType getByType() {
        return DateShiftType.YEAR;
    }
}
