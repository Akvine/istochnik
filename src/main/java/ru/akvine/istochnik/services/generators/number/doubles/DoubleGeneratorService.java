package ru.akvine.istochnik.services.generators.number.doubles;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.number.doubles.configs.DoubleGeneratorConfig;
import ru.akvine.istochnik.services.generators.number.doubles.random.DoubleRandomGenerator;
import ru.akvine.istochnik.services.generators.number.doubles.shift.DoubleRangeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoubleGeneratorService {
    private final DoubleRandomGenerator doubleRandomGenerator;
    private final DoubleRangeService doubleRangeService;

    public List<Double> generate(DoubleGeneratorConfig config) {
        List<Double> generatedValues;
        if (config.getRangeType() == RangeType.RANDOM) {
            generatedValues = doubleRandomGenerator.generate(config);
        } else {
            generatedValues = doubleRangeService.range(
                    config.getDoubleShiftRange().getStart(),
                    config.getDoubleShiftRange().getEnd(),
                    config.getDoubleShiftRange().getStep());
        }

        return generatedValues;
    }
}
