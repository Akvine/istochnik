package ru.akvine.istochnik.api.detectors;

import ru.akvine.istochnik.enums.BaseType;

import java.util.List;

public interface Detector<T> {
    boolean isRandom(List<T> values);

    boolean isShifted(List<T> values);

    BaseType getByType();
}
