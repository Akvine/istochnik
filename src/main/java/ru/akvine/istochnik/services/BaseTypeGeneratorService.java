package ru.akvine.istochnik.services;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.generators.number.doubles.DoubleGeneratorService;
import ru.akvine.istochnik.services.generators.number.doubles.configs.DoubleGeneratorConfig;
import ru.akvine.istochnik.services.generators.number.integer.IntegerGeneratorService;
import ru.akvine.istochnik.services.generators.number.integer.configs.IntegerGeneratorConfig;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BaseTypeGeneratorService {
    private final ConfigMapperService configMapperService;
    private final DoubleGeneratorService doubleGeneratorService;
    private final IntegerGeneratorService integerGeneratorService;

    public List<?> generate(BaseType type, Config config, List<Filter> filters) {
        return switch (type) {
            case INTEGER -> {
                IntegerGeneratorConfig integerGeneratorConfig = configMapperService.createIntegerConfig(config);
                yield integerGeneratorService.generate(integerGeneratorConfig, filters);
            }
            case DOUBLE -> {
                DoubleGeneratorConfig doubleGeneratorConfig = configMapperService.createDoubleConfig(config);
                yield doubleGeneratorService.generate(doubleGeneratorConfig, filters);
            }
            case STRING -> {
                List<Object> generatedValues = new ArrayList<>();
                for (int i = 0; i < config.getSize(); ++i) {
                    generatedValues.add(RandomStringUtils.randomAlphanumeric(config.getLength()));
                }

                yield generatedValues;
            }
        };
    }
}
