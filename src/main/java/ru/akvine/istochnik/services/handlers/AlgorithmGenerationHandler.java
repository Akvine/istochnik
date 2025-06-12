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
import ru.akvine.istochnik.services.dto.Converter;
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
        List<Converter> converters = generateColumn.getConverters();

        List<?> generatedValues;
        if (baseType != null) {
            generatedValues = baseTypeGeneratorServicesProvider.get(baseType).generate(config, converters);
        } else {
            generatedValues = customTypeGeneratorServicesProvider.get(customType).generate(config, converters);
        }
        return generatedValues;
    }

    @Override
    public GenerationStrategy getStrategy() {
        return GenerationStrategy.ALGORITHM;
    }
}
