package ru.akvine.istochnik.services.generators.bool.configs;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BooleanConstantConfig {
    @Nullable
    private Boolean constant;
    private int size;
}
