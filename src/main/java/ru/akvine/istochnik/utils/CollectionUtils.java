package ru.akvine.istochnik.utils;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@UtilityClass
public class CollectionUtils {
    public <T> T getRandomElement(Set<T> elements) {
        return getRandomElement(elements, new SecureRandom());
    }

    public <T> T getRandomElement(Set<T> elements, Random random) {
        return getRandomElement(new ArrayList<>(elements), random);
    }

    public <T> T getRandomElement(List<T> elements) {
        return getRandomElement(elements, new SecureRandom());
    }

    public <T> T getRandomElement(List<T> elements, Random random) {
        int randomIndex = random.nextInt(elements.size());
        return elements.get(randomIndex);
    }
}
