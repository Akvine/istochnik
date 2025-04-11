package ru.akvine.istochnik.services;

import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Filter;

import java.util.List;

public interface CustomTypeGeneratorService {
    List<?> generate(Config config, List<Filter> filters);

    CustomType getType();
}
