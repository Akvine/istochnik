package ru.akvine.istochnik.services.generators.custom.decimal;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.exceptions.GenerationException;
import ru.akvine.istochnik.services.generators.custom.decimal.configs.DecimalGeneratorConfig;
import ru.akvine.istochnik.services.generators.custom.decimal.random.DecimalRandomGenerator;
import ru.akvine.istochnik.services.generators.custom.decimal.shift.DecimalRangeService;

@Service
@RequiredArgsConstructor
public class DecimalGeneratorService {
    private final DecimalRandomGenerator decimalRandomGenerator;
    private final DecimalRangeService decimalRangeService;

    public List<BigDecimal> generate(DecimalGeneratorConfig config) {
        if (config.getRangeType() == RangeType.RANDOM) {
            return decimalRandomGenerator.generate(config);
        }

        DecimalShiftRange shiftRange = config.getDecimalShiftRange();
        BigDecimal start = shiftRange.getStart().setScale(config.getScale());
        BigDecimal step = shiftRange.getStep().setScale(config.getScale());
        if (step.signum() <= 0) {
            throw new GenerationException("field [step] must be greater than 0");
        }

        // Правая граница не дальше, чем нужно для size значений
        BigDecimal sizeEnd = start.add(step.multiply(BigDecimal.valueOf(config.getSize())));
        BigDecimal end = shiftRange.getEnd() == null ? sizeEnd : shiftRange.getEnd().min(sizeEnd);

        return decimalRangeService.range(start, end, step);
    }
}
