package ru.akvine.istochnik.services.mappers;

import java.math.BigDecimal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.generators.custom.decimal.DecimalShiftRange;
import ru.akvine.istochnik.services.generators.custom.decimal.configs.DecimalGeneratorConfig;

@Service
public class DecimalConfigMapperService implements ConfigMapperService<DecimalGeneratorConfig> {
    @Override
    public DecimalGeneratorConfig map(Config config) {
        Asserts.isNotNull(config, "config is null");

        BigDecimal start = new BigDecimal(config.getStart().trim());
        BigDecimal end = StringUtils.isBlank(config.getEnd())
                ? null
                : new BigDecimal(config.getEnd().trim());
        BigDecimal step = StringUtils.isBlank(config.getStep())
                ? BigDecimal.ONE
                : new BigDecimal(config.getStep().trim());

        // Количество знаков после запятой берётся из значений, которыми задан диапазон
        int scale = Math.max(start.scale(), 0);
        if (end != null) {
            scale = Math.max(scale, end.scale());
        }
        if (config.getRangeType() == RangeType.SHIFT) {
            scale = Math.max(scale, step.scale());
        }

        return new DecimalGeneratorConfig(
                config.getSize(),
                config.isNotNull(),
                config.isUnique(),
                config.getRangeType(),
                new DecimalShiftRange().setStart(start).setEnd(end).setStep(step),
                scale,
                config.getRandomGenerator());
    }

    @Override
    public String getType() {
        return CustomType.DECIMAL.getName();
    }
}
