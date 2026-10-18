package ru.akvine.istochnik.services.mappers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.utils.Asserts;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.generators.custom.zoneddatetime.ZonedDateTimeShiftRange;
import ru.akvine.istochnik.services.generators.custom.zoneddatetime.configs.ZonedDateTimeGeneratorConfig;
import ru.akvine.istochnik.utils.ZonedDateTimeUtils;

@Service
public class ZonedDateTimeConfigMapperService implements ConfigMapperService<ZonedDateTimeGeneratorConfig> {
    @Override
    public ZonedDateTimeGeneratorConfig map(Config config) {
        Asserts.isNotNull(config, "config is null");
        return new ZonedDateTimeGeneratorConfig(
                config.getSize(),
                config.isNotNull(),
                config.isUnique(),
                config.getRangeType(),
                new ZonedDateTimeShiftRange()
                        .setStart(ZonedDateTimeUtils.parse(config.getStart()))
                        .setEnd(StringUtils.isBlank(config.getEnd()) ? null : ZonedDateTimeUtils.parse(config.getEnd()))
                        .setShiftCount(
                                StringUtils.isBlank(config.getStep())
                                        ? 1
                                        : Long.parseLong(config.getStep().trim())),
                config.getRandomGenerator())
                .setDistribution(config.getDistribution());
    }

    @Override
    public String getType() {
        return CustomType.ZONED_DATETIME.getName();
    }
}
