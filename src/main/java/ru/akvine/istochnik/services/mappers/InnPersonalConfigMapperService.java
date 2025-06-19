package ru.akvine.istochnik.services.mappers;

import org.springframework.stereotype.Service;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.generators.custom.inn.personal.InnPersGeneratorConfig;

@Service
public class InnPersonalConfigMapperService implements ConfigMapperService<InnPersGeneratorConfig> {
    @Override
    public InnPersGeneratorConfig map(Config config) {
        Asserts.isNotNull(config);
        return new InnPersGeneratorConfig(
                config.getSize(),
                config.isNotNull(),
                config.isUnique(),
                config.isValid(),
                config.getRandomGenerator()
        );
    }

    @Override
    public String getType() {
        return CustomType.INN_PERSONAL.getName();
    }
}
