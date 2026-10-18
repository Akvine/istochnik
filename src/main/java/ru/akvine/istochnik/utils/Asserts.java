package ru.akvine.istochnik.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class Asserts {
    public void isNotNull(Object object) {
        String message = object.getClass().getSimpleName() + " is null!";
        isNotNull(object, message);
    }

    public void isNotNull(Object object, String message) {
        isNotNull(object, new IllegalArgumentException(message));
    }

    public <T extends RuntimeException> void isNotNull(Object object, T exception) {
        if (Objects.isNull(object)) {
            throw exception;
        }
    }

    public void isNotBlank(String content) {
        isNotBlank(content, "content is blank or null!");
    }

    public void isNotBlank(String content, String message) {
        isNotBlank(content, new IllegalArgumentException(message));
    }

    public <T extends RuntimeException> void isNotBlank(String content, T exception) {
        if (StringUtils.isBlank(content)) {
            throw exception;
        }
    }

    public void isNotEmpty(Collection<?> collection) {
        isNotEmpty(collection, "collection is empty or null!");
    }

    public void isNotEmpty(Collection<?> collection, String message) {
        isNotEmpty(collection, new IllegalArgumentException(message));
    }

    public <T extends RuntimeException> void isNotEmpty(Collection<?> collection, T exception) {
        if (collection == null || collection.isEmpty()) {
            throw exception;
        }
    }

    public void isNotEmpty(Map<?, ?> map) {
        isNotEmpty(map, "map is empty or null!");
    }

    public void isNotEmpty(Map<?, ?> map, String message) {
        isNotEmpty(map, new IllegalArgumentException(message));
    }

    public <T extends RuntimeException> void isNotEmpty(Map<?, ?> map, T exception) {
        if (map == null || map.isEmpty()) {
            throw exception;
        }
    }
}
