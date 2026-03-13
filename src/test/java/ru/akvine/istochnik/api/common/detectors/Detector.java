package ru.akvine.istochnik.api.common.detectors;

import java.util.List;
import ru.akvine.istochnik.enums.BaseType;

public interface Detector<T> {
    boolean isRandom(List<T> values);

    boolean isShifted(List<T> values);

    BaseType getByType();
}
