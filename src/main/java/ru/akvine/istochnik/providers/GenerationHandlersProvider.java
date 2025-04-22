package ru.akvine.istochnik.providers;

import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;
import ru.akvine.istochnik.services.GenerationHandler;

import java.util.Map;

public record GenerationHandlersProvider(Map<GenerationStrategy, GenerationHandler> handlers) {
    public GenerationHandler getByType(GenerationStrategy type) {
        if (handlers.containsKey(type)) {
            return handlers.get(type);
        }

        throw new UnsupportedTypeGenerationException("Generation handler for strategy = [" + type + "] is not supported!");
    }
}
