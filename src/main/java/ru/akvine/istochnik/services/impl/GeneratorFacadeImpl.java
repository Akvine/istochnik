package ru.akvine.istochnik.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.managers.BaseTypeGeneratorServicesManager;
import ru.akvine.istochnik.managers.CustomTypeGeneratorServicesManager;
import ru.akvine.istochnik.services.GeneratorFacade;
import ru.akvine.istochnik.services.dto.*;
import ru.akvine.istochnik.services.generators.ConstantGenerator;
import ru.akvine.istochnik.utils.Asserts;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneratorFacadeImpl implements GeneratorFacade {
    private final CustomTypeGeneratorServicesManager customTypeGeneratorServicesManager;
    private final BaseTypeGeneratorServicesManager baseTypeGeneratorServicesManager;

    @Override
    public Table generate(GenerateData generateData) {
        Asserts.isNotNull(generateData, "generateData is null");

        Table table = new Table(generateData.getSize());
        for (GenerateColumn generateColumn : generateData.getGenerateColumns()) {
            BaseType baseType = generateColumn.getBaseType();
            CustomType customType = generateColumn.getCustomType();
            Config config = generateColumn.getConfig();
            List<Filter> filters = generateColumn.getFilters();
            GenerationStrategy strategy = generateColumn.getGenerationStrategy();

            List<?> generatedValues;

            if (strategy == GenerationStrategy.CONSTANT) {
                generatedValues = generate(config);
            } else {
                if (baseType != null) {
                    generatedValues = baseTypeGeneratorServicesManager.get(baseType).generate(config, filters);
                } else {
                    generatedValues = customTypeGeneratorServicesManager.get(customType).generate(config, filters);
                }
            }

            table.addColumn(generateColumn.getName(), generatedValues);
        }

        return table;
    }

    public List<?> generate(Config config) {
        return new ConstantGenerator<>().generate(config.getSize(), config.getConstant());
    }
}
