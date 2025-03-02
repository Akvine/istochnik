package ru.akvine.istochnik.services.generators.date;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.managers.DateRangeServicesManager;
import ru.akvine.istochnik.services.generators.ConstantGenerator;
import ru.akvine.istochnik.services.generators.date.configs.DateConstantConfig;
import ru.akvine.istochnik.services.generators.date.configs.DateGeneratorConfig;
import ru.akvine.istochnik.services.generators.date.random.DateRandomGenerator;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DateGeneratorService {
    private final DateRandomGenerator dateRandomGenerator;
    private final DateRangeServicesManager dateRangeServicesManager;

    public List<LocalDate> generate(DateConstantConfig config) {
        return new ConstantGenerator<LocalDate>().generate(config.getSize(), config.getConstant());
    }

    public List<LocalDate> generate(DateGeneratorConfig config) {
        if (config.getRangeType() == RangeType.RANDOM) {
            return dateRandomGenerator.generate(config);
        } else {
            DateShiftRange shiftRange = config.getDateShiftRange();
            return (List<LocalDate>) dateRangeServicesManager.getByType(shiftRange.getDateShiftType())
                    .range(shiftRange.getStart(),
                            shiftRange.getEnd(),
                            Math.toIntExact(shiftRange.getShiftCount())
                    );
        }
    }
}
