package ru.akvine.istochnik.services.mappers;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.generators.inn.personal.InnPersGeneratorConfig;
import ru.akvine.istochnik.utils.Asserts;

@Service
public class InnPersonalConfigMapperService implements ConfigMapperService<InnPersGeneratorConfig> {
    @Override
    public InnPersGeneratorConfig map(Config config) {
        Asserts.isNotNull(config);
        return new InnPersGeneratorConfig(
                config.getSize(),
                config.isNotNull(),
                config.isUnique(),
                config.isValid()
        );
    }

    @Override
    public String getType() {
        return CustomType.INN_PERSONAL.getName();
    }
}
