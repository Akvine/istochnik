package ru.akvine.istochnik.services.generators.number.integer.configs;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class IntegerConstantsConfig {
    @Nullable
    private Integer value;
    private int size;
}
