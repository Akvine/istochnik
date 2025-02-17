package ru.akvine.istochnik.services.generators.number.integer;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.ConstantGenerator;
import ru.akvine.istochnik.services.generators.number.integer.configs.IntegerConstantsConfig;
import ru.akvine.istochnik.services.generators.number.integer.configs.IntegerGeneratorConfig;
import ru.akvine.istochnik.services.generators.number.integer.random.IntegerRandomGenerator;
import ru.akvine.istochnik.services.generators.number.integer.shift.AbstractIntegerRangeService;

import java.util.List;

@Service
public class IntegerGeneratorService {
    private final IntegerRandomGenerator integerRandomGenerator;
    private final AbstractIntegerRangeService<Integer, Integer> integerRangeService;

    public IntegerGeneratorService(IntegerRandomGenerator integerRandomGenerator,
                                   AbstractIntegerRangeService<Integer, Integer> integerRangeService) {
        this.integerRandomGenerator = integerRandomGenerator;
        this.integerRangeService = integerRangeService;
    }

    public List<Integer> generate(IntegerConstantsConfig config) {
        return new ConstantGenerator<Integer>().generate(config.getSize(), config.getValue());
    }

    public List<Integer> generate(IntegerGeneratorConfig config) {
        if (config.getRangeType() == RangeType.RANDOM) {
            return (List<Integer>) integerRandomGenerator.generate(config);
        } else {
            return (List<Integer>) integerRangeService.range(
                    config.getIntegerRange().getStart(),
                    config.getIntegerRange().getEnd(),
                    config.getIntegerRange().getStep());
        }
    }
}
