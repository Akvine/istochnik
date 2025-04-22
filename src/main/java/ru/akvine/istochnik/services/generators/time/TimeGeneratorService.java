package ru.akvine.istochnik.services.generators.time;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.providers.TimeRangeServicesProvider;
import ru.akvine.istochnik.services.generators.time.configs.TimeGeneratorConfig;
import ru.akvine.istochnik.services.generators.time.random.TimeRandomGenerator;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeGeneratorService {
    private final TimeRandomGenerator timeRandomGenerator;
    private final TimeRangeServicesProvider timeRangeServicesProvider;

    public List<LocalTime> generate(TimeGeneratorConfig config) {
        if (config.getRangeType() == RangeType.RANDOM) {
            return timeRandomGenerator.generate(config);
        } else {
            TimeShiftRange shiftRange = config.getTimeShiftRange();
            return (List<LocalTime>) timeRangeServicesProvider.getByType(shiftRange.getDateShiftType())
                    .range(shiftRange.getStart(), shiftRange.getEnd(), shiftRange.getShiftCount());
        }
    }
}
