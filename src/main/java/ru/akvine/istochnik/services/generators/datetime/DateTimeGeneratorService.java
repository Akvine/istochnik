package ru.akvine.istochnik.services.generators.datetime;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.managers.DateTimeRangeServicesManager;
import ru.akvine.istochnik.services.generators.datetime.configs.DateTimeGeneratorConfig;
import ru.akvine.istochnik.services.generators.datetime.random.DateTimeRandomGenerator;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DateTimeGeneratorService {
    private final DateTimeRandomGenerator dateTimeRandomGenerator;
    private final DateTimeRangeServicesManager dateTimeRangeServicesManager;

    public DateTimeGeneratorService(DateTimeRandomGenerator dateTimeRandomGenerator,
                                    DateTimeRangeServicesManager dateTimeRangeServicesManager) {
        this.dateTimeRandomGenerator = dateTimeRandomGenerator;
        this.dateTimeRangeServicesManager = dateTimeRangeServicesManager;
    }

    public List<LocalDateTime> generate(DateTimeGeneratorConfig config) {
        if (config.getRangeType() == RangeType.RANDOM) {
            return dateTimeRandomGenerator.generate(config);
        } else {
            DateTimeShiftRange shiftRange = config.getDateTimeShiftRange();
            return (List<LocalDateTime>) dateTimeRangeServicesManager.getByType(shiftRange.getDateShiftType())
                    .range(shiftRange.getStart(), shiftRange.getEnd(), shiftRange.getShiftCount());
        }
    }
}
