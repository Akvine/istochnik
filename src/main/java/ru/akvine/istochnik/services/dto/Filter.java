package ru.akvine.istochnik.services.dto;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Filter {
    private String name;

    @Nullable
    private String argument1;

    @Nullable
    private String argument2;
}
