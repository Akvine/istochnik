package ru.akvine.istochnik.controllers.dto.converters;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ConverterDto {
    private String name;
    private Object[] arguments = new Object[0];
    private int probability = 100;
}
