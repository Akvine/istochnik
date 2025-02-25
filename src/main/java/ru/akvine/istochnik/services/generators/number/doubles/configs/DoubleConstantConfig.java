package ru.akvine.istochnik.services.generators.number.doubles.configs;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DoubleConstantConfig {
    @Nullable
    private Double value;
    private int size;
}
