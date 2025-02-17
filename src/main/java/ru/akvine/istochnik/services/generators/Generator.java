package ru.akvine.istochnik.services.generators;

import java.util.Collection;

public interface Generator<T, C extends Config> {
    Collection<T> generate(C config);

    Class<?> getType();
}
