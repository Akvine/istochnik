package ru.akvine.istochnik.controllers.dto.converters;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ConverterTypeDto {
    private String type;
    private List<String> names;
    private int size;
}
