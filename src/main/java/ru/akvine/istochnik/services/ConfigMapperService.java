package ru.akvine.istochnik.services;

import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.generators.date.localdatetime.configs.LocalDateTimeGeneratorConfig;
import ru.akvine.istochnik.services.generators.number.doubles.configs.DoubleGeneratorConfig;
import ru.akvine.istochnik.services.generators.number.integer.configs.IntegerGeneratorConfig;

public interface ConfigMapperService {
    LocalDateTimeGeneratorConfig createLocalDateTimeConfig(Config config);

    IntegerGeneratorConfig createIntegerConfig(Config config);

    DoubleGeneratorConfig createDoubleConfig(Config config);
}
