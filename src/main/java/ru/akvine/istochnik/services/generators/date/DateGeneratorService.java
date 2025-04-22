package ru.akvine.istochnik.services.generators.date;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.providers.DateRangeServicesProvider;
import ru.akvine.istochnik.services.generators.date.configs.DateGeneratorConfig;
import ru.akvine.istochnik.services.generators.date.random.DateRandomGenerator;

import java.time.LocalDate;
import java.util.List;

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
            return (List<LocalDate>) dateRangeServicesProvider.getByType(shiftRange.getDateShiftType())
                    .range(shiftRange.getStart(),
                            shiftRange.getEnd(),
                            Math.toIntExact(shiftRange.getShiftCount())
                    );
        }
    }
}
