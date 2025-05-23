package ru.akvine.istochnik.services.generators.custom.time.shift;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.DateShiftType;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class TimeMinutesRangeService extends AbstractTimeRangeService<LocalTime, Integer> {
    @Override
    public Collection<LocalTime> range(LocalTime start, LocalTime end, Integer step) {
        List<LocalTime> range = new ArrayList<>();
        for (LocalTime time = start; time.isBefore(end) || time.isAfter(end); time = time.plusMinutes(step)) {
            range.add(time);
        }
        return range;
    }

    @Override
    public DateShiftType getByType() {
        return DateShiftType.MINUTE;
    }
}
