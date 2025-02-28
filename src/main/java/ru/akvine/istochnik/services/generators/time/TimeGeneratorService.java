package ru.akvine.istochnik.services.generators.time;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.managers.TimeRangeServicesManager;
import ru.akvine.istochnik.services.generators.ConstantGenerator;
import ru.akvine.istochnik.services.generators.time.configs.TimeConstantsConfig;
import ru.akvine.istochnik.services.generators.time.configs.TimeGeneratorConfig;
import ru.akvine.istochnik.services.generators.time.random.TimeRandomGenerator;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeGeneratorService {
    private final TimeRandomGenerator timeRandomGenerator;
    private final TimeRangeServicesManager timeRangeServicesManager;

    public List<LocalTime> generate(TimeConstantsConfig config) {
        return new ConstantGenerator<LocalTime>().generate(config.getSize(), config.getConstant());
    }

    public List<LocalTime> generate(TimeGeneratorConfig config) {
        if (config.getRangeType() == RangeType.RANDOM) {
            return timeRandomGenerator.generate(config);
        } else {
            TimeShiftRange shiftRange = config.getTimeShiftRange();
            return (List<LocalTime>) timeRangeServicesManager.getByType(shiftRange.getDateShiftType())
                    .range(shiftRange.getStart(), shiftRange.getEnd(), shiftRange.getShiftCount());
        }
    }
}
