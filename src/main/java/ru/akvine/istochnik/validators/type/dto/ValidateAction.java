package ru.akvine.istochnik.validators.type.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.compozit.commons.istochnik.ConverterDto;

import java.util.Collection;
import java.util.List;

@Data
@Accessors(chain = true)
public final class ValidateAction {
    private String start;
    private String end;
    private String step;
    private String rangeType;
    private int rowsCount;
    private Collection<ConverterDto> converters = List.of();
}
