package ru.akvine.istochnik.services.generators.custom.date.shift;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.DateShiftType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DateDaysRangeService extends AbstractDateRangeService<LocalDate, Integer> {
    @Override
    public List<LocalDate> range(LocalDate start, LocalDate end, Integer step) {
        List<LocalDate> range = new ArrayList<>();
        for (LocalDate date = start; date.isBefore(end) || date.isEqual(end); date = date.plusDays(step)) {
            range.add(date);
        }
        return range;
    }

    @Override
    public DateShiftType getByType() {
        return DateShiftType.DAY;
    }
}
