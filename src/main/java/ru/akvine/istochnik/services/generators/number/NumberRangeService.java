package ru.akvine.istochnik.services.generators.number;

import java.util.Collection;

public interface NumberRangeService<T, E extends Number> {
    Collection<T> range(T start, T end, E step);
}
