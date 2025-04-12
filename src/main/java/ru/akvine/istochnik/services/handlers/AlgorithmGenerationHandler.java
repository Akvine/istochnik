package ru.akvine.istochnik.services.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.managers.BaseTypeGeneratorServicesManager;
import ru.akvine.istochnik.managers.CustomTypeGeneratorServicesManager;
import ru.akvine.istochnik.services.GenerationHandler;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.dto.GenerateColumn;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlgorithmGenerationHandler implements GenerationHandler {
    private final CustomTypeGeneratorServicesManager customTypeGeneratorServicesManager;
    private final BaseTypeGeneratorServicesManager baseTypeGeneratorServicesManager;


    @Override
    public List<?> handle(GenerateColumn generateColumn) {
        BaseType baseType = generateColumn.getBaseType();
        CustomType customType = generateColumn.getCustomType();
        Config config = generateColumn.getConfig();
        List<Filter> filters = generateColumn.getFilters();

        List<?> generatedValues;
        if (baseType != null) {
            generatedValues = baseTypeGeneratorServicesManager.get(baseType).generate(config, filters);
        } else {
            generatedValues = customTypeGeneratorServicesManager.get(customType).generate(config, filters);
        }
        return generatedValues;
    }

    @Override
    public GenerationStrategy getStrategy() {
        return GenerationStrategy.ALGORITHM;
    }
}
