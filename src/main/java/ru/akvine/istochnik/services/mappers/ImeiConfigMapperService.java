package ru.akvine.istochnik.services.mappers;

import org.springframework.stereotype.Service;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.generators.custom.imei.ImeiGeneratorConfig;

@Service
public class ImeiConfigMapperService implements ConfigMapperService<ImeiGeneratorConfig> {
    @Override
    public ImeiGeneratorConfig map(Config config) {
        Asserts.isNotNull(config);
        return new ImeiGeneratorConfig(config.getSize(),
                config.isNotNull(),
                config.isUnique(),
                config.isValid(),
                config.getRandomGenerator());
    }

    @Override
    public String getType() {
        return CustomType.IMEI.getName();
    }
}
