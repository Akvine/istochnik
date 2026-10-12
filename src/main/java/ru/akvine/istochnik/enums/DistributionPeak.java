package ru.akvine.istochnik.enums;

import jakarta.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Граница диапазона, у которой значения несимметричного распределения встречаются чаще всего
 */
@Getter
@AllArgsConstructor
public enum DistributionPeak {
    START("start"),
    END("end");

    private final String name;

    @Nullable
    public static DistributionPeak from(String name) {
        for (DistributionPeak peak : values()) {
            if (peak.getName().equalsIgnoreCase(name)) {
                return peak;
            }
        }

        return null;
    }

    public static List<String> names() {
        return Arrays.stream(values()).map(DistributionPeak::getName).toList();
    }
}
