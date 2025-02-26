package ru.akvine.istochnik.services.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Filter {
    private String name;

    private String argument1;

    private String argument2;
}
