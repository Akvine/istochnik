package ru.akvine.istochnik.services.generators.date.localdatetime.configs;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class LocalDateTimeConstantsConfig {
    @Nullable
    private LocalDateTime constant;
    private int size;
}
