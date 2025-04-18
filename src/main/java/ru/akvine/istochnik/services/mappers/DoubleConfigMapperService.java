package ru.akvine.istochnik.services.mappers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.generators.number.doubles.DoubleShiftRange;
import ru.akvine.istochnik.services.generators.number.doubles.configs.DoubleGeneratorConfig;

@Service
public class DoubleConfigMapperService implements ConfigMapperService<DoubleGeneratorConfig> {
    @Override
    public DoubleGeneratorConfig map(Config config) {
        Asserts.isNotNull(config);
        return new DoubleGeneratorConfig(
                config.getSize(),
                config.isNotNull(),
                config.isUnique(),
                config.getRangeType(),
                new DoubleShiftRange()
                        .setStart(Double.parseDouble(config.getStart()))
                        .setEnd(Double.parseDouble(config.getEnd()))
                        .setStep(StringUtils.isBlank(config.getStep()) ? 1 : Double.parseDouble(config.getStep()))
        );
    }

    @Override
    public String getType() {
        return BaseType.DOUBLE.getValue();
    }
}
