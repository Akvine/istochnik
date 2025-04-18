package ru.akvine.istochnik.services.mappers;

import org.springframework.stereotype.Service;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.generators.snils.configs.SnilsGeneratorConfig;

@Service
public class SnilsConfigMapperService implements ConfigMapperService<SnilsGeneratorConfig> {
    @Override
    public SnilsGeneratorConfig map(Config config) {
        Asserts.isNotNull(config);
        return new SnilsGeneratorConfig(
                config.getSize(),
                config.isNotNull(),
                config.isUnique(),
                config.isValid()
        );
    }

    @Override
    public String getType() {
        return CustomType.SNILS.getName();
    }
}
