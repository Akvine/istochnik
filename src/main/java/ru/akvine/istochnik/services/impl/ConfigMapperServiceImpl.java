package ru.akvine.istochnik.services.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.ConfigMapperService;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.generators.bool.BooleanShiftRange;
import ru.akvine.istochnik.services.generators.bool.configs.BooleanGeneratorConfig;
import ru.akvine.istochnik.services.generators.date.DateShiftRange;
import ru.akvine.istochnik.services.generators.date.configs.DateGeneratorConfig;
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
                        .setShiftCount(StringUtils.isBlank(config.getStep()) ? 1 : Integer.parseInt(config.getStep()))
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
                        .setStep(StringUtils.isBlank(config.getStep()) ? 1 : Integer.parseInt(config.getStep())),
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
                        .setStep(StringUtils.isBlank(config.getStep()) ? 1 : Double.parseDouble(config.getStep()))
        );
    }

    @Override
    public BooleanGeneratorConfig createBooleanConfig(Config config) {
        Asserts.isNotNull(config, "config is null");
        return new BooleanGeneratorConfig(
                config.getSize(),
                config.getNotNull(),
                config.getUnique(),
                config.getRangeType(),
                new BooleanShiftRange()
                        .setStart(Boolean.parseBoolean(config.getStart()))
        );
    }

    @Override
    public DateGeneratorConfig createDateConfig(Config config) {
        Asserts.isNotNull(config, "config is null");
        return new DateGeneratorConfig(
                config.getSize(),
                config.getNotNull(),
                config.getUnique(),
                config.getRangeType(),
                new DateShiftRange()
                        .setStart(DateTimeUtils.toLocalDate(config.getStart()))
                        .setEnd(DateTimeUtils.toLocalDate(config.getEnd()))
                        .setShiftCount(StringUtils.isBlank(config.getStep()) ? 1 : Integer.parseInt(config.getStep()))
        );
    }
}
