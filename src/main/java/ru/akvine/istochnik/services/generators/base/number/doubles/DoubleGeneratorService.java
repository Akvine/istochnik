package ru.akvine.istochnik.services.generators.base.number.doubles;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.exceptions.GenerationException;
import ru.akvine.istochnik.services.generators.base.number.doubles.configs.DoubleGeneratorConfig;
import ru.akvine.istochnik.services.generators.base.number.doubles.random.DoubleRandomGenerator;
import ru.akvine.istochnik.services.generators.base.number.doubles.shift.DoubleRangeService;

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
            if (config.getDoubleShiftRange().getStep() <= 0) {
                throw new GenerationException("field [step] must be greater than 0");
            }

            generatedValues = doubleRangeService.range(
                    config.getDoubleShiftRange().getStart(),
                    config.getDoubleShiftRange().getEnd(),
                    config.getDoubleShiftRange().getStep());
            if (generatedValues.size() > config.getSize()) {
                generatedValues = generatedValues.subList(0, config.getSize());
            }
        }

        return generatedValues;
    }
}
