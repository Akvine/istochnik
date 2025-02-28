package ru.akvine.istochnik.services;

import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.generators.datetime.configs.DateTimeGeneratorConfig;
import ru.akvine.istochnik.services.generators.time.configs.TimeGeneratorConfig;
import ru.akvine.istochnik.services.generators.number.doubles.configs.DoubleGeneratorConfig;
import ru.akvine.istochnik.services.generators.number.integer.configs.IntegerGeneratorConfig;

public interface ConfigMapperService {
    DateTimeGeneratorConfig createDateTimeConfig(Config config);

    TimeGeneratorConfig createTimeGeneratorConfig(Config config);

    IntegerGeneratorConfig createIntegerConfig(Config config);

    DoubleGeneratorConfig createDoubleConfig(Config config);
}
