package ru.akvine.istochnik.services.generators.custom.date;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.exceptions.GenerationException;
import ru.akvine.istochnik.providers.DateRangeServicesProvider;
import ru.akvine.istochnik.services.generators.custom.date.configs.DateGeneratorConfig;
import ru.akvine.istochnik.services.generators.custom.date.random.DateRandomGenerator;

@Service
@RequiredArgsConstructor
public class DateGeneratorService {
    private final DateRandomGenerator dateRandomGenerator;
    private final DateRangeServicesProvider dateRangeServicesProvider;

    public List<LocalDate> generate(DateGeneratorConfig config) {
        if (config.getRangeType() == RangeType.RANDOM) {
            return dateRandomGenerator.generate(config);
        } else {
            DateShiftRange shiftRange = config.getDateShiftRange();
            if (shiftRange.getShiftCount() <= 0) {
                throw new GenerationException("field [step] must be greater than 0");
            }

            List<LocalDate> range = (List<LocalDate>) dateRangeServicesProvider
                    .getByType(shiftRange.getDateShiftType())
                    .range(shiftRange.getStart(), shiftRange.getEnd(), Math.toIntExact(shiftRange.getShiftCount()));
            return range.size() > config.getSize() ? range.subList(0, config.getSize()) : range;
        }
    }
}
