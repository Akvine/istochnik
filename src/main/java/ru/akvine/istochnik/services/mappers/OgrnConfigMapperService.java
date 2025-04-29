package ru.akvine.istochnik.services.mappers;

import org.springframework.stereotype.Service;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.generators.custom.ogrn.OgrnGeneratorConfig;

@Service
public class OgrnConfigMapperService implements ConfigMapperService<OgrnGeneratorConfig> {
    @Override
    public OgrnGeneratorConfig map(Config config) {
        Asserts.isNotNull(config);
        return new OgrnGeneratorConfig(
                config.getSize(),
                config.isNotNull(),
                config.isUnique(),
                config.isValid()
        );
    }

    @Override
    public String getType() {
        return CustomType.OGRN.getName();
    }
}
