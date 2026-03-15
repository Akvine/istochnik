package ru.akvine.istochnik.services.generators.base.number;

import java.util.Collection;

public interface NumberRangeService<T, E extends Number> {
    // TODO: добавить поддерку параметров notNull и unique
    Collection<T> range(T start, T end, E step);
}
