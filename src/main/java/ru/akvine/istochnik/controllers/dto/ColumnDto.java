package ru.akvine.istochnik.controllers.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ColumnDto {
    private String name;

    private String type;

    private ConfigDto config;

    private List<FilterDto> filters;
}
