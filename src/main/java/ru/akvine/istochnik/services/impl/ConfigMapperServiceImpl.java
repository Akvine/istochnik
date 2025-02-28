package ru.akvine.istochnik.services.impl;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.ConfigMapperService;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.generators.datetime.DateTimeShiftRange;
import ru.akvine.istochnik.services.generators.datetime.configs.DateTimeGeneratorConfig;
import ru.akvine.istochnik.services.generators.number.doubles.DoubleShiftRange;
import ru.akvine.istochnik.services.generators.number.doubles.configs.DoubleGeneratorConfig;
import ru.akvine.istochnik.services.generators.number.integer.IntegerShiftRange;
import ru.akvine.istochnik.services.generators.number.integer.configs.IntegerGeneratorConfig;
import ru.akvine.istochnik.services.generators.time.TimeShiftRange;
import ru.akvine.istochnik.services.generators.time.configs.TimeGeneratorConfig;
import ru.akvine.istochnik.utils.Asserts;
import ru.akvine.istochnik.utils.DateTimeUtils;

@Service
public class ConfigMapperServiceImpl implements ConfigMapperService {
    @Override
    public DateTimeGeneratorConfig createDateTimeConfig(Config config) {
        Asserts.isNotNull(config, "config is null");
        return new DateTimeGeneratorConfig(
                config.getSize(),
                config.getNotNull(),
                config.getUnique(),
                config.getRangeType(),
                new DateTimeShiftRange()
                        .setStart(DateTimeUtils.toLocalDateTime(config.getStart()))
                        .setEnd(DateTimeUtils.toLocalDateTime(config.getEnd()))
                        .setShiftCount(Integer.parseInt(config.getStep()))
        );
    }

    @Override
    public TimeGeneratorConfig createTimeGeneratorConfig(Config config) {
        Asserts.isNotNull(config);
        return new TimeGeneratorConfig(
                config.getSize(),
                config.getNotNull(),
                config.getUnique(),
                config.getRangeType(),
                new TimeShiftRange()
                        .setStart(DateTimeUtils.toLocalTime(config.getStart()))
                        .setEnd(DateTimeUtils.toLocalTime((config.getEnd())))
                        .setShiftCount(Integer.parseInt(config.getStep()))
        );
    }

    @Override
    public IntegerGeneratorConfig createIntegerConfig(Config config) {
        Asserts.isNotNull(config, "config is null");
        return new IntegerGeneratorConfig(
                config.getSize(),
                config.getNotNull(),
                config.getUnique(),
                config.getRangeType(),
                new IntegerShiftRange()
                        .setStart(Integer.parseInt(config.getStart()))
                        .setEnd(Integer.parseInt(config.getEnd()))
                        .setStep(Integer.parseInt(config.getStep())),
                config.getFilters()
        );
    }

    @Override
    public DoubleGeneratorConfig createDoubleConfig(Config config) {
        Asserts.isNotNull(config);
        return new DoubleGeneratorConfig(
                config.getSize(),
                config.getNotNull(),
                config.getUnique(),
                config.getRangeType(),
                new DoubleShiftRange()
                        .setStart(Double.parseDouble(config.getStart()))
                        .setEnd(Double.parseDouble(config.getEnd()))
                        .setStep(Double.parseDouble(config.getStep()))
        );
    }
}
