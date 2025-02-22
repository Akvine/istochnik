package ru.akvine.istochnik.controllers.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ConfigDto {
    private int size;

    private Boolean unique;

    private Boolean notNull;

    private String rangeType;

    private String start;

    private String end;

    private Integer step;
}
