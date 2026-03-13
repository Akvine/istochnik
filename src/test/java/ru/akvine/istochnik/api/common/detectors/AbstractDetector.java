package ru.akvine.istochnik.api.common.detectors;

import java.util.List;
import java.util.Objects;
import org.springframework.util.CollectionUtils;

public abstract class AbstractDetector<T> implements Detector<T> {
    protected List<T> clearOfNull(List<T> values) {
        if (CollectionUtils.isEmpty(values)) {
            throw new IllegalArgumentException("Values is empty!");
        }

        return values.stream().filter(Objects::nonNull).toList();
    }
}
