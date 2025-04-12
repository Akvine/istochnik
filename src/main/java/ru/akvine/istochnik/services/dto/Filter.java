package ru.akvine.istochnik.services.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.FilterType;

@Data
@Accessors(chain = true)
public class Filter {
    private FilterType name;

    private Object[] arguments;
}
