package ru.akvine.istochnik.services.mappers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.generators.date.DateShiftRange;
import ru.akvine.istochnik.services.generators.date.configs.DateGeneratorConfig;
import ru.akvine.istochnik.utils.Asserts;
import ru.akvine.istochnik.utils.DateTimeUtils;

@Service
public class DateConfigMapperService implements ConfigMapperService<DateGeneratorConfig> {
    @Override
    public DateGeneratorConfig map(Config config) {
        Asserts.isNotNull(config, "config is null");
        return new DateGeneratorConfig(
                config.getSize(),
                config.isNotNull(),
                config.isUnique(),
                config.getRangeType(),
                new DateShiftRange()
                        .setStart(DateTimeUtils.toLocalDate(config.getStart()))
                        .setEnd(DateTimeUtils.toLocalDate(config.getEnd()))
                        .setShiftCount(StringUtils.isBlank(config.getStep()) ? 1 : Integer.parseInt(config.getStep()))
        );
    }

    @Override
    public String getType() {
        return CustomType.DATE.getName();
    }
}
