package ru.akvine.istochnik.services.impl;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.ConfigMapperService;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.generators.date.localdatetime.LocalDateTimeShiftRange;
import ru.akvine.istochnik.services.generators.date.localdatetime.configs.LocalDateTimeGeneratorConfig;
import ru.akvine.istochnik.services.generators.number.integer.IntegerShiftRange;
import ru.akvine.istochnik.services.generators.number.integer.configs.IntegerGeneratorConfig;
import ru.akvine.istochnik.utils.Asserts;
import ru.akvine.istochnik.utils.DateTimeUtils;

@Service
public class ConfigMapperServiceImpl implements ConfigMapperService {
    @Override
    public LocalDateTimeGeneratorConfig generateLocalDateTimeConfig(Config config) {
        Asserts.isNotNull(config, "config is null");
        return new LocalDateTimeGeneratorConfig(
                config.getSize(),
                config.getNotNull(),
                config.getUnique(),
                config.getRangeType(),
                new LocalDateTimeShiftRange()
                        .setStart(DateTimeUtils.toLocalDateTime(config.getStart()))
                        .setEnd(DateTimeUtils.toLocalDateTime(config.getEnd()))
                        .setShiftCount(config.getStep())
        );
    }

    @Override
    public IntegerGeneratorConfig generateIntegerConfig(Config config) {
        Asserts.isNotNull(config, "config is null");
        return new IntegerGeneratorConfig(
                config.getSize(),
                config.getNotNull(),
                config.getUnique(),
                config.getRangeType(),
                new IntegerShiftRange()
                        .setStart(Integer.parseInt(config.getStart()))
                        .setEnd(Integer.parseInt(config.getEnd()))
                        .setStep(config.getStep())
        );
    }
}
