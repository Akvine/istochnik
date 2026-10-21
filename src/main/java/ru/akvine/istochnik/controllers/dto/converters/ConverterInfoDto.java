package ru.akvine.istochnik.controllers.dto.converters;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ConverterInfoDto {
    private String name;
    private String description;
    private ConverterExampleDto example;
}
