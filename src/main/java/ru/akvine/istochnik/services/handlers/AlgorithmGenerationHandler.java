package ru.akvine.istochnik.services.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.providers.BaseTypeGeneratorServicesProvider;
import ru.akvine.istochnik.providers.CustomTypeGeneratorServicesProvider;
import ru.akvine.istochnik.services.GenerationHandler;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.dto.GenerateColumn;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlgorithmGenerationHandler implements GenerationHandler {
    private final CustomTypeGeneratorServicesProvider customTypeGeneratorServicesProvider;
    private final BaseTypeGeneratorServicesProvider baseTypeGeneratorServicesProvider;


    @Override
    public List<?> handle(GenerateColumn generateColumn) {
        BaseType baseType = generateColumn.getBaseType();
        CustomType customType = generateColumn.getCustomType();
        Config config = generateColumn.getConfig();
        List<Filter> filters = generateColumn.getFilters();

        List<?> generatedValues;
        if (baseType != null) {
            generatedValues = baseTypeGeneratorServicesProvider.get(baseType).generate(config, filters);
        } else {
            generatedValues = customTypeGeneratorServicesProvider.get(customType).generate(config, filters);
        }
        return generatedValues;
    }

    @Override
    public GenerationStrategy getStrategy() {
        return GenerationStrategy.ALGORITHM;
    }
}
