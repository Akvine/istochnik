package ru.akvine.istochnik.services.mappers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.utils.Asserts;
import ru.akvine.istochnik.utils.DateTimeUtils;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.DateShiftType;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.generators.custom.datetime.DateTimeShiftRange;
import ru.akvine.istochnik.services.generators.custom.datetime.configs.DateTimeGeneratorConfig;

@Service
public class DateTimeConfigMapperService implements ConfigMapperService<DateTimeGeneratorConfig> {
    @Override
    public DateTimeGeneratorConfig map(Config config) {
        Asserts.isNotNull(config, "config is null");
        return new DateTimeGeneratorConfig(
                config.getSize(),
                config.isNotNull(),
                config.isUnique(),
                config.getRangeType(),
                new DateTimeShiftRange()
                        .setStart(DateTimeUtils.toLocalDateTime(config.getStart()))
                        .setEnd(DateTimeUtils.toLocalDateTime(config.getEnd()))
                        .setDateShiftType(DateShiftType.SECOND)
                        .setShiftCount(StringUtils.isBlank(config.getStep()) ? 1 : Integer.parseInt(config.getStep())),
                config.getRandomGenerator())
                .setDistribution(config.getDistribution());
    }

    @Override
    public String getType() {
        return CustomType.DATETIME.getName();
    }
}
