package ru.akvine.istochnik.services.generators.custom.time;

import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.exceptions.GenerationException;
import ru.akvine.istochnik.providers.TimeRangeServicesProvider;
import ru.akvine.istochnik.services.generators.custom.time.configs.TimeGeneratorConfig;
import ru.akvine.istochnik.services.generators.custom.time.random.TimeRandomGenerator;

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
            if (shiftRange.getShiftCount() <= 0) {
                throw new GenerationException("field [step] must be greater than 0");
            }

            List<LocalTime> range = (List<LocalTime>) timeRangeServicesProvider
                    .getByType(shiftRange.getDateShiftType())
                    .range(shiftRange.getStart(), shiftRange.getEnd(), shiftRange.getShiftCount());
            return range.size() > config.getSize() ? range.subList(0, config.getSize()) : range;
        }
    }
}
