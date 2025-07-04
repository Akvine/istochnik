package ru.akvine.istochnik.services.generators;

import org.springframework.beans.factory.annotation.Value;
import ru.akvine.istochnik.exceptions.GenerationException;

public abstract class AbstractRandomGenerator<T, C extends Config> implements Generator<T, C> {

    @Value("${max.generation.attempts}")
    private int maxGenerationAttempts;

    protected void checkGenerationCountAttempts(int iteration, int size) {
        if (iteration / size > maxGenerationAttempts) {
            String message = String.format(
                    "Max attempts to generation [%s] is exceeded for [%s] column type",
                    maxGenerationAttempts,
                    getName()
            );
            throw new GenerationException(message);
        }
    }
}
