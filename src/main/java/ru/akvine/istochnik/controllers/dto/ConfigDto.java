package ru.akvine.istochnik.controllers.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ConfigDto {
    private boolean unique;

    private boolean notNull;

    private boolean valid;

    private String rangeType;

    private String start;

    private String end;

    private String step;

    private Integer length;

    private Object constant;
}
