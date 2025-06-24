package ru.akvine.istochnik.services.mappers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.generators.base.number.integer.IntegerShiftRange;
import ru.akvine.istochnik.services.generators.base.number.integer.configs.IntegerGeneratorConfig;

@Service
public class IntegerConfigMapperService implements ConfigMapperService<IntegerGeneratorConfig> {
    @Override
    public IntegerGeneratorConfig map(Config config) {
        Asserts.isNotNull(config, "config is null");
        return new IntegerGeneratorConfig(
                config.getSize(),
                config.isNotNull(),
                config.isUnique(),
                config.getRangeType(),
                new IntegerShiftRange()
                        .setStart(Long.parseLong(config.getStart()))
                        .setEnd(Long.parseLong(config.getEnd()))
                        .setStep(StringUtils.isBlank(config.getStep()) ? 1 : Long.parseLong(config.getStep())),
                config.getConverters(),
                config.getRandomGenerator()
        );
    }

    @Override
    public String getType() {
        return BaseType.INTEGER.getValue();
    }
}
