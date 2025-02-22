package ru.akvine.istochnik.services.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.RangeType;

@Data
@Accessors(chain = true)
public class Config {
    private int size;

    private Boolean unique;

    private Boolean notNull;

    private RangeType rangeType;

    private String start;

    private String end;

    private Integer step;
}
