package ru.akvine.istochnik.services.mappers;

import org.springframework.stereotype.Service;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.generators.base.bool.BooleanShiftRange;
import ru.akvine.istochnik.services.generators.base.bool.configs.BooleanGeneratorConfig;

@Service
public class BooleanConfigMapperService implements ConfigMapperService<BooleanGeneratorConfig>{
    @Override
    public BooleanGeneratorConfig map(Config config) {
        Asserts.isNotNull(config, "config is null");
        return new BooleanGeneratorConfig(
                config.getSize(),
                config.isNotNull(),
                config.isUnique(),
                config.getRangeType(),
                new BooleanShiftRange()
                        .setStart(Boolean.parseBoolean(config.getStart())),
                config.getRandomGenerator()
        );
    }

    @Override
    public String getType() {
        return BaseType.BOOLEAN.getValue();
    }
}
