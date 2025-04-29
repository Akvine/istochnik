package ru.akvine.istochnik.services.generators.base.number.integer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.base.number.integer.shift.AbstractIntegerRangeService;
import ru.akvine.istochnik.services.generators.base.number.integer.configs.IntegerGeneratorConfig;
import ru.akvine.istochnik.services.generators.base.number.integer.random.IntegerRandomGenerator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IntegerGeneratorService {
    private final IntegerRandomGenerator integerRandomGenerator;
    private final AbstractIntegerRangeService<Integer, Integer> integerRangeService;

    public List<Integer> generate(IntegerGeneratorConfig config) {
        List<Integer> generatedValues;

        if (config.getRangeType() == RangeType.RANDOM) {
            generatedValues = (List<Integer>) integerRandomGenerator.generate(config);
        } else {
            generatedValues = (List<Integer>) integerRangeService.range(
                    config.getIntegerShiftRange().getStart(),
                    config.getIntegerShiftRange().getEnd(),
                    config.getIntegerShiftRange().getStep());
        }

        return generatedValues;
    }
}
