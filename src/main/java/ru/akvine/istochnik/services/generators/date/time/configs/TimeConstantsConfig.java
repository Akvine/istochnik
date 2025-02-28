package ru.akvine.istochnik.services.generators.date.time.configs;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalTime;

@Data
@Accessors(chain = true)
public class TimeConstantsConfig {
    @Nullable
    private LocalTime constant;
    private int size;
}
