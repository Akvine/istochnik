package ru.akvine.istochnik.enums;

import jakarta.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Закон распределения случайных значений внутри диапазона
 */
@Getter
@AllArgsConstructor
public enum DistributionType {
    UNIFORM("uniform"),
    NORMAL("normal"),
    EXPONENTIAL("exponential"),
    ZIPF("zipf");

    private final String name;

    @Nullable
    public static DistributionType from(String name) {
        for (DistributionType type : values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }

        return null;
    }

    public static List<String> names() {
        return Arrays.stream(values()).map(DistributionType::getName).toList();
    }
}
