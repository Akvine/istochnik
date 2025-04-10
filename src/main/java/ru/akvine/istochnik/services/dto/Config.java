package ru.akvine.istochnik.services.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.RangeType;

import java.util.List;

@Data
@Accessors(chain = true)
public class Config {
    private int size;

    private boolean unique;

    private boolean notNull;

    private boolean valid;

    private RangeType rangeType;

    private String start;

    private String end;

    private String step;

    private List<Filter> filters;

    private Integer length;
}
