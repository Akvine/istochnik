package ru.akvine.istochnik.services.generators;

import java.util.Collection;

public interface Generator<T, C extends Config> {
    Collection<T> generate(C config);

    default String getName() {
        return this.getClass().getSimpleName();
    }
}
