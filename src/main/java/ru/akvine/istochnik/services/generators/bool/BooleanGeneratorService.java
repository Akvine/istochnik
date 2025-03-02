package ru.akvine.istochnik.services.generators.bool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.ConstantGenerator;
import ru.akvine.istochnik.services.generators.bool.configs.BooleanConstantConfig;
import ru.akvine.istochnik.services.generators.bool.configs.BooleanGeneratorConfig;
import ru.akvine.istochnik.services.generators.bool.random.BooleanRandomGenerator;
import ru.akvine.istochnik.services.generators.bool.shift.BooleanShiftRangeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BooleanGeneratorService {
    private final BooleanShiftRangeService booleanShiftRangeService;
    private final BooleanRandomGenerator booleanRandomGenerator;

    public List<Boolean> generate(BooleanConstantConfig config) {
        return new ConstantGenerator<Boolean>().generate(config.getSize(), config.getConstant());
    }

    public List<Boolean> generate(BooleanGeneratorConfig config) {
        if (config.getRangeType() == RangeType.RANDOM) {
            return booleanRandomGenerator.generate(config);
        } else {
            BooleanShiftRange range = config.getBooleanShiftRange();
            return booleanShiftRangeService.range(range.isStart(), config.getSize());
        }
    }
}
