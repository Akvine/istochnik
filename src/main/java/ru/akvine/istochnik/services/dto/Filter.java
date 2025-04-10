package ru.akvine.istochnik.services.dto;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.FilterType;

@Data
@Accessors(chain = true)
public class Filter {
    private FilterType name;

    @Nullable
    private String argument1;

    @Nullable
    private String argument2;
}
