package ru.akvine.istochnik.providers;

import java.util.Map;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.validators.strategy.GenerationStrategyValidator;

public record GenerationStrategyValidatorsProvider(Map<GenerationStrategy, GenerationStrategyValidator> validators) {
    public GenerationStrategyValidator getByType(GenerationStrategy type) {
        if (validators.containsKey(type)) {
            return validators.get(type);
        }

        return null;
    }
}
