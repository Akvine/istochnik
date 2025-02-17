package ru.akvine.istochnik.managers;

import ru.akvine.istochnik.enums.DateShiftType;
import ru.akvine.istochnik.services.generators.date.localdatetime.shift.AbstractLocalDateTimeRangeService;

import java.time.LocalDateTime;
import java.util.Map;

public record LocalDateTimeRangeServicesManager(Map<DateShiftType, AbstractLocalDateTimeRangeService<LocalDateTime, Long>> services) {
    public AbstractLocalDateTimeRangeService<LocalDateTime, Long> getByType(DateShiftType type) {
        if (services.containsKey(type)) {
            return services.get(type);
        }

        throw new IllegalArgumentException("Local date time service by shift type = [" + type + "] is not supported!");
    }
}
