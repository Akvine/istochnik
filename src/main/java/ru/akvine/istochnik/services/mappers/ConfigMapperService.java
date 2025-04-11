package ru.akvine.istochnik.services.mappers;

import ru.akvine.istochnik.services.dto.Config;

public interface ConfigMapperService<T extends ru.akvine.istochnik.services.generators.Config> {
    T map(Config config);

    String getType();
}
