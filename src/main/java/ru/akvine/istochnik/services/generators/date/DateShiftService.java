package ru.akvine.istochnik.services.generators.date;

import ru.akvine.istochnik.enums.DateShiftType;

import java.util.Collection;

public interface DateShiftService<T, E extends Number> {
    Collection<T> shift(T start, T end, E step);

    DateShiftType getByType();
}
