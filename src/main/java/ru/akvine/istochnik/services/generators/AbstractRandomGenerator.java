package ru.akvine.istochnik.services.generators;

import java.security.SecureRandom;
import java.util.random.RandomGenerator;

public abstract class AbstractRandomGenerator<T, C extends Config> implements Generator<T, C> {
    protected final RandomGenerator randomGenerator = new SecureRandom();
}
