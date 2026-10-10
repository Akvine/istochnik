package ru.akvine.istochnik.services.mappers;

import org.springframework.stereotype.Service;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.generators.custom.zoneid.ZoneIdGeneratorConfig;

@Service
public class ZoneIdConfigMapperService implements ConfigMapperService<ZoneIdGeneratorConfig> {
    @Override
    public ZoneIdGeneratorConfig map(Config config) {
        Asserts.isNotNull(config);
        return new ZoneIdGeneratorConfig(
                config.getSize(),
                config.isNotNull(),
                config.isUnique(),
                config.getRangeType(),
                config.getRandomGenerator());
    }

    @Override
    public String getType() {
        return CustomType.ZONE_ID.getName();
    }
}
