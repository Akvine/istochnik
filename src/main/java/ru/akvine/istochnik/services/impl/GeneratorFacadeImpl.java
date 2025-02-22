package ru.akvine.istochnik.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Type;
import ru.akvine.istochnik.services.ConfigMapperService;
import ru.akvine.istochnik.services.GeneratorFacade;
import ru.akvine.istochnik.services.dto.GenerateColumn;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.GenerateData;
import ru.akvine.istochnik.services.dto.Table;
import ru.akvine.istochnik.services.generators.date.localdatetime.LocalDateTimeGeneratorService;
import ru.akvine.istochnik.services.generators.date.localdatetime.configs.LocalDateTimeGeneratorConfig;
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

    private final ConfigMapperService configMapperService;


    @Override
    public Table generate(GenerateData generateData) {
        Asserts.isNotNull(generateData, "generateData is null");

        Table table = new Table(generateData.getSize());
        for (GenerateColumn generateColumn : generateData.getGenerateColumns()) {
            Type type = generateColumn.getType();
            Config config = generateColumn.getConfig();

            List<?> generatedValues = switch (type) {
                case LOCALDATETIME -> {
                    LocalDateTimeGeneratorConfig localDateTimeGeneratorConfig = configMapperService.generateLocalDateTimeConfig(config);
                    yield localDateTimeGeneratorService.generate(localDateTimeGeneratorConfig);
                }
                case INTEGER -> {
                    IntegerGeneratorConfig integerGeneratorConfig = configMapperService.generateIntegerConfig(config);
                    yield integerGeneratorService.generate(integerGeneratorConfig);
                }
                case UUID -> uuidGeneratorService.generate(config.getSize());
            };

            table.addColumn(generateColumn.getName(), generatedValues);
        }
        
        return table;
    }
}
