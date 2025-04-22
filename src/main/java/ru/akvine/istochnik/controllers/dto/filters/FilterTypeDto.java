package ru.akvine.istochnik.controllers.dto.filters;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class FilterTypeDto {
    private String type;
    private List<String> names;
    private int size;
}
