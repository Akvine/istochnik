package ru.akvine.istochnik.services.mappers;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.generators.inn.org.InnOrgGeneratorConfig;
import ru.akvine.istochnik.utils.Asserts;

@Service
public class InnOrgConfigMapperService implements ConfigMapperService<InnOrgGeneratorConfig> {
    @Override
    public InnOrgGeneratorConfig map(Config config) {
        Asserts.isNotNull(config);
        return new InnOrgGeneratorConfig(
                config.getSize(),
                config.isNotNull(),
                config.isUnique(),
                config.isValid()
        );
    }

    @Override
    public String getType() {
        return CustomType.INN_ORG.getName();
    }
}
