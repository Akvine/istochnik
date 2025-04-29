package ru.akvine.istochnik.providers;

import ru.akvine.istochnik.enums.DateShiftType;
import ru.akvine.istochnik.services.generators.custom.datetime.shift.AbstractDateTimeRangeService;

import java.time.LocalDateTime;
import java.util.Map;

public record DateTimeRangeServicesProvider(Map<DateShiftType, AbstractDateTimeRangeService<LocalDateTime, Long>> services) {
    public AbstractDateTimeRangeService<LocalDateTime, Long> getByType(DateShiftType type) {
        if (services.containsKey(type)) {
            return services.get(type);
        }

        throw new IllegalArgumentException("Local date time service by shift type = [" + type + "] is not supported!");
    }
}
