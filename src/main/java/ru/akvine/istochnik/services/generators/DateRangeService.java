package ru.akvine.istochnik.services.generators;

import ru.akvine.istochnik.enums.DateShiftType;

import java.util.Collection;

public interface DateRangeService<T, E extends Number> {
    Collection<T> range(T start, T end, E step);

    DateShiftType getByType();
}
