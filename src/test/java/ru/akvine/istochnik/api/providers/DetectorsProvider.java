package ru.akvine.istochnik.api.providers;

import ru.akvine.istochnik.api.detectors.Detector;
import ru.akvine.istochnik.enums.BaseType;

import java.util.Map;

public record DetectorsProvider(Map<BaseType, Detector<?>> detectors) {
    public Detector<?> get(BaseType type) {
        if (detectors.containsKey(type)) {
            return detectors.get(type);
        }

        throw new IllegalArgumentException("Detector for base type = [" + type + "] is not supported by app!");
    }
}
