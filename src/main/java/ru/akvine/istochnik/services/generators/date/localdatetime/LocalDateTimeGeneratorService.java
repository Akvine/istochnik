package ru.akvine.istochnik.services.generators.date.localdatetime;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.managers.LocalDateTimeShiftServicesManager;
import ru.akvine.istochnik.services.generators.ConstantGenerator;
import ru.akvine.istochnik.services.generators.date.localdatetime.configs.LocalDateTimeConstantsConfig;
import ru.akvine.istochnik.services.generators.date.localdatetime.configs.LocalDateTimeGeneratorConfig;
import ru.akvine.istochnik.services.generators.date.localdatetime.random.LocalDateTimeRandomGenerator;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LocalDateTimeGeneratorService {
    private final LocalDateTimeRandomGenerator localDateTimeRandomGenerator;
    private final LocalDateTimeShiftServicesManager localDateTimeShiftServicesManager;

    public LocalDateTimeGeneratorService(LocalDateTimeRandomGenerator localDateTimeRandomGenerator, LocalDateTimeShiftServicesManager localDateTimeShiftServicesManager) {
        this.localDateTimeRandomGenerator = localDateTimeRandomGenerator;
        this.localDateTimeShiftServicesManager = localDateTimeShiftServicesManager;
    }

    public List<LocalDateTime> generate(LocalDateTimeConstantsConfig config) {
        return new ConstantGenerator<LocalDateTime>().generate(config.getSize(), config.getConstant());
    }

    public List<LocalDateTime> generate(LocalDateTimeGeneratorConfig config) {
        if (config.getRangeType() == RangeType.RANDOM) {
            return localDateTimeRandomGenerator.generate(config);
        } else {
            LocalDateTimeShiftRange shiftRange = config.getLocalDateTimeShiftRange();
            return (List<LocalDateTime>) localDateTimeShiftServicesManager.getByType(shiftRange.getDateShiftType())
                    .shift(shiftRange.getStart(), shiftRange.getEnd(), shiftRange.getShiftCount());
        }
    }
}
