package ru.akvine.istochnik.providers;

import java.time.LocalTime;
import java.util.Map;
import ru.akvine.istochnik.enums.DateShiftType;
import ru.akvine.istochnik.services.generators.custom.time.shift.AbstractTimeRangeService;

public record TimeRangeServicesProvider(Map<DateShiftType, AbstractTimeRangeService<LocalTime, Integer>> services) {
    public AbstractTimeRangeService<LocalTime, Integer> getByType(DateShiftType type) {
        if (services.containsKey(type)) {
            return services.get(type);
        }

        throw new IllegalArgumentException("Time service by shift type = [" + type + "] is not supported!");
    }
}
