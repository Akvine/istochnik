package ru.akvine.istochnik.services.generators;

import java.util.Collection;
import ru.akvine.istochnik.enums.DateShiftType;

public interface DateRangeService<T, E extends Number> {
    Collection<T> range(T start, T end, E step);

    DateShiftType getByType();
}
