package ru.akvine.istochnik.providers;

import ru.akvine.istochnik.enums.DateShiftType;
import ru.akvine.istochnik.services.generators.date.shift.AbstractDateRangeService;

import java.time.LocalDate;
import java.util.Map;

public record DateRangeServicesProvider(Map<DateShiftType, AbstractDateRangeService<LocalDate, Integer>> services) {
    public AbstractDateRangeService<LocalDate, Integer> getByType(DateShiftType type) {
        if (services.containsKey(type)) {
            return services.get(type);
        }

        throw new IllegalArgumentException("Time service by shift type = [" + type + "] is not supported!");
    }
}
