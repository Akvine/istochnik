package ru.akvine.istochnik.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.services.BaseTypeGeneratorService;
import ru.akvine.istochnik.services.CustomTypeGeneratorService;
import ru.akvine.istochnik.services.GeneratorFacade;
import ru.akvine.istochnik.services.dto.*;
import ru.akvine.istochnik.utils.Asserts;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneratorFacadeImpl implements GeneratorFacade {
    private final CustomTypeGeneratorService customTypeGeneratorService;
    private final BaseTypeGeneratorService baseTypeGeneratorService;

    @Override
    public Table generate(GenerateData generateData) {
        Asserts.isNotNull(generateData, "generateData is null");

        Table table = new Table(generateData.getSize());
        for (GenerateColumn generateColumn : generateData.getGenerateColumns()) {
            BaseType baseType = generateColumn.getBaseType();
            CustomType customType = generateColumn.getCustomType();
            Config config = generateColumn.getConfig();
            List<Filter> filters = generateColumn.getFilters();

            List<?> generatedValues;
            if (baseType != null) {
                generatedValues = baseTypeGeneratorService.generate(baseType, config, filters);
            } else {
                generatedValues = customTypeGeneratorService.generate(customType, config, filters);
            }

            table.addColumn(generateColumn.getName(), generatedValues);
        }

        return table;
    }
}
