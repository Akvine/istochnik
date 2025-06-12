package ru.akvine.istochnik.controllers.dto.converters;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.compozit.commons.dto.SuccessfulResponse;

import java.util.List;

@Data
@Accessors(chain = true)
public class ConvertersListResponse extends SuccessfulResponse {
    private List<ConverterTypeDto> converters;
}
