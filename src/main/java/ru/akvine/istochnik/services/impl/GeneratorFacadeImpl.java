package ru.akvine.istochnik.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Type;
import ru.akvine.istochnik.services.ConfigMapperService;
import ru.akvine.istochnik.services.GeneratorFacade;
import ru.akvine.istochnik.services.dto.*;
import ru.akvine.istochnik.services.generators.date.localdatetime.LocalDateTimeGeneratorService;
import ru.akvine.istochnik.services.generators.date.localdatetime.configs.LocalDateTimeGeneratorConfig;
import ru.akvine.istochnik.services.generators.number.doubles.DoubleGeneratorService;
import ru.akvine.istochnik.services.generators.number.doubles.configs.DoubleGeneratorConfig;
import ru.akvine.istochnik.services.generators.number.integer.IntegerGeneratorService;
import ru.akvine.istochnik.services.generators.number.integer.configs.IntegerGeneratorConfig;
import ru.akvine.istochnik.services.generators.uuid.UuidGeneratorService;
import ru.akvine.istochnik.utils.Asserts;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneratorFacadeImpl implements GeneratorFacade {
    private final LocalDateTimeGeneratorService localDateTimeGeneratorService;
    private final IntegerGeneratorService integerGeneratorService;
    private final UuidGeneratorService uuidGeneratorService;
    private final DoubleGeneratorService doubleGeneratorService;

    private final ConfigMapperService configMapperService;


    @Override
    public Table generate(GenerateData generateData) {
        Asserts.isNotNull(generateData, "generateData is null");

        Table table = new Table(generateData.getSize());
        for (GenerateColumn generateColumn : generateData.getGenerateColumns()) {
            Type type = generateColumn.getType();
            Config config = generateColumn.getConfig();
            List<Filter> filters = generateColumn.getFilters();

            List<?> generatedValues = switch (type) {
                case LOCALDATETIME -> {
                    LocalDateTimeGeneratorConfig localDateTimeGeneratorConfig = configMapperService.createLocalDateTimeConfig(config);
                    yield localDateTimeGeneratorService.generate(localDateTimeGeneratorConfig);
                }
                case INTEGER -> {
                    IntegerGeneratorConfig integerGeneratorConfig = configMapperService.createIntegerConfig(config);
                    yield integerGeneratorService.generate(integerGeneratorConfig, filters);
                }
                case DOUBLE -> {
                    DoubleGeneratorConfig doubleGeneratorConfig = configMapperService.createDoubleConfig(config);
                    yield doubleGeneratorService.generate(doubleGeneratorConfig);
                }
                case UUID -> uuidGeneratorService.generate(config.getSize(), filters);
            };

            table.addColumn(generateColumn.getName(), generatedValues);
        }
        
        return table;
    }
}
