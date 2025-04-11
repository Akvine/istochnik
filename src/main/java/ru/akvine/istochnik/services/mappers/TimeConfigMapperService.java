package ru.akvine.istochnik.services.mappers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.generators.time.TimeShiftRange;
import ru.akvine.istochnik.services.generators.time.configs.TimeGeneratorConfig;
import ru.akvine.istochnik.utils.Asserts;
import ru.akvine.istochnik.utils.DateTimeUtils;

@Service
public class TimeConfigMapperService implements ConfigMapperService<TimeGeneratorConfig> {
    @Override
    public TimeGeneratorConfig map(Config config) {
        Asserts.isNotNull(config);
        return new TimeGeneratorConfig(
                config.getSize(),
                config.isNotNull(),
                config.isUnique(),
                config.getRangeType(),
                new TimeShiftRange()
                        .setStart(DateTimeUtils.toLocalTime(config.getStart()))
                        .setEnd(DateTimeUtils.toLocalTime((config.getEnd())))
                        .setShiftCount(StringUtils.isBlank(config.getStep()) ? 1 : Integer.parseInt(config.getStep()))
        );
    }

    @Override
    public String getType() {
        return CustomType.TIME.getName();
    }
}
