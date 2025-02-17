package ru.akvine.istochnik.services.generators.date.localdatetime;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.managers.LocalDateTimeRangeServicesManager;
import ru.akvine.istochnik.services.generators.ConstantGenerator;
import ru.akvine.istochnik.services.generators.date.localdatetime.configs.LocalDateTimeConstantsConfig;
import ru.akvine.istochnik.services.generators.date.localdatetime.configs.LocalDateTimeGeneratorConfig;
import ru.akvine.istochnik.services.generators.date.localdatetime.random.LocalDateTimeRandomGenerator;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LocalDateTimeGeneratorService {
    private final LocalDateTimeRandomGenerator localDateTimeRandomGenerator;
    private final LocalDateTimeRangeServicesManager localDateTimeRangeServicesManager;

    public LocalDateTimeGeneratorService(LocalDateTimeRandomGenerator localDateTimeRandomGenerator, LocalDateTimeRangeServicesManager localDateTimeRangeServicesManager) {
        this.localDateTimeRandomGenerator = localDateTimeRandomGenerator;
        this.localDateTimeRangeServicesManager = localDateTimeRangeServicesManager;
    }

    public List<LocalDateTime> generate(LocalDateTimeConstantsConfig config) {
        return new ConstantGenerator<LocalDateTime>().generate(config.getSize(), config.getConstant());
    }

    public List<LocalDateTime> generate(LocalDateTimeGeneratorConfig config) {
        if (config.getRangeType() == RangeType.RANDOM) {
            return localDateTimeRandomGenerator.generate(config);
        } else {
            LocalDateTimeShiftRange shiftRange = config.getLocalDateTimeShiftRange();
            return (List<LocalDateTime>) localDateTimeRangeServicesManager.getByType(shiftRange.getDateShiftType())
                    .range(shiftRange.getStart(), shiftRange.getEnd(), shiftRange.getShiftCount());
        }
    }
}
