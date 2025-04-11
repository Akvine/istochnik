package ru.akvine.istochnik.services;

import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Filter;

import java.util.List;

public interface BaseTypeGeneratorService {
    List<?> generate(Config config, List<Filter> filters);

    BaseType getType();
}
