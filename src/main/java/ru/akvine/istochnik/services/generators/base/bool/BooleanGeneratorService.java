package ru.akvine.istochnik.services.generators.base.bool;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.base.bool.configs.BooleanGeneratorConfig;
import ru.akvine.istochnik.services.generators.base.bool.random.BooleanRandomGenerator;
import ru.akvine.istochnik.services.generators.base.bool.shift.BooleanShiftRangeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BooleanGeneratorService {
    private final BooleanShiftRangeService booleanShiftRangeService;
    private final BooleanRandomGenerator booleanRandomGenerator;

    public List<Boolean> generate(BooleanGeneratorConfig config) {
        if (config.getRangeType() == RangeType.RANDOM) {
            return booleanRandomGenerator.generate(config);
        } else {
            BooleanShiftRange range = config.getBooleanShiftRange();
            return booleanShiftRangeService.range(range.isStart(), config.getSize());
        }
    }
}
