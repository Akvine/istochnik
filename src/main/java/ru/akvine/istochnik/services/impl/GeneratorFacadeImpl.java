package ru.akvine.istochnik.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.managers.GenerationHandlersManager;
import ru.akvine.istochnik.services.GeneratorFacade;
import ru.akvine.istochnik.services.dto.GenerateColumn;
import ru.akvine.istochnik.services.dto.GenerateData;
import ru.akvine.istochnik.services.dto.Table;
import ru.akvine.istochnik.utils.Asserts;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneratorFacadeImpl implements GeneratorFacade {
    private final GenerationHandlersManager generationHandlersManager;

    @Override
    public Table generate(GenerateData generateData) {
        Asserts.isNotNull(generateData, "generateData is null");

        Table table = new Table(generateData.getSize());
        for (GenerateColumn generateColumn : generateData.getGenerateColumns()) {

            GenerationStrategy strategy = generateColumn.getGenerationStrategy();
            List<?> generatedValues = generationHandlersManager.getByType(strategy).handle(generateColumn);

            table.addColumn(generateColumn.getName(), generatedValues);
        }

        return table;
    }
}
