package ru.akvine.istochnik.services.generators.date.configs;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class DateConstantConfig {
    @Nullable
    private LocalDate constant;
    private int size;
}
