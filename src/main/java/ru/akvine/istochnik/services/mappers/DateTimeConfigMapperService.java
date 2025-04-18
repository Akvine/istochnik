package ru.akvine.istochnik.services.mappers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.compozit.commons.utils.DateTimeUtils;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.generators.datetime.DateTimeShiftRange;
import ru.akvine.istochnik.services.generators.datetime.configs.DateTimeGeneratorConfig;

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
                        .setShiftCount(StringUtils.isBlank(config.getStep()) ? 1 : Integer.parseInt(config.getStep()))
        );
    }

    @Override
    public String getType() {
        return CustomType.DATETIME.getName();
    }
}
