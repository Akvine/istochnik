package ru.akvine.istochnik.validators.type.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Collection;

@Data
@Accessors(chain = true)
public final class ValidateAction {
    private String start;
    private String end;
    private String step;
    private String rangeType;
    private int rowsCount;
    private Collection<String> filters;
}
