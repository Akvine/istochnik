package ru.akvine.istochnik.controllers.dto.converters;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ConverterTypeDto {
    private String type;
    private List<String> names;
    private int size;
}
