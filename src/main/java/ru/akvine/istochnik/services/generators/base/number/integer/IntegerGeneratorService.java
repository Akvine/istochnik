package ru.akvine.istochnik.services.generators.base.number.integer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.base.number.integer.configs.IntegerGeneratorConfig;
import ru.akvine.istochnik.services.generators.base.number.integer.random.IntegerRandomGenerator;
import ru.akvine.istochnik.services.generators.base.number.integer.shift.AbstractIntegerRangeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IntegerGeneratorService {
    private final IntegerRandomGenerator integerRandomGenerator;
    private final AbstractIntegerRangeService<Long, Long> integerRangeService;

    public List<Long> generate(IntegerGeneratorConfig config) {
        List<Long> generatedValues;

        if (config.getRangeType() == RangeType.RANDOM) {
            generatedValues = (List<Long>) integerRandomGenerator.generate(config);
        } else {
            generatedValues = (List<Long>) integerRangeService.range(
                    config.getIntegerShiftRange().getStart(),
                    config.getIntegerShiftRange().getEnd(),
                    config.getIntegerShiftRange().getStep());
        }

        return generatedValues;
    }
}
