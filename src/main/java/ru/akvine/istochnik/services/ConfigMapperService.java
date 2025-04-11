package ru.akvine.istochnik.services;

import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.generators.bool.configs.BooleanGeneratorConfig;
import ru.akvine.istochnik.services.generators.date.configs.DateGeneratorConfig;
import ru.akvine.istochnik.services.generators.datetime.configs.DateTimeGeneratorConfig;
import ru.akvine.istochnik.services.generators.inn.org.InnOrgGeneratorConfig;
import ru.akvine.istochnik.services.generators.snils.configs.SnilsGeneratorConfig;
import ru.akvine.istochnik.services.generators.time.configs.TimeGeneratorConfig;
import ru.akvine.istochnik.services.generators.number.doubles.configs.DoubleGeneratorConfig;
import ru.akvine.istochnik.services.generators.number.integer.configs.IntegerGeneratorConfig;

public interface ConfigMapperService {
    DateTimeGeneratorConfig createDateTimeConfig(Config config);

    TimeGeneratorConfig createTimeGeneratorConfig(Config config);

    IntegerGeneratorConfig createIntegerConfig(Config config);

    DoubleGeneratorConfig createDoubleConfig(Config config);

    BooleanGeneratorConfig createBooleanConfig(Config config);

    DateGeneratorConfig createDateConfig(Config config);

    SnilsGeneratorConfig createSnilsGeneratorConfig(Config config);

    InnOrgGeneratorConfig createInnOrgGeneratorConfig(Config config);
}
