package ru.akvine.istochnik.controllers.dto.converters;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.controllers.dto.SuccessfulResponse;

@Data
@Accessors(chain = true)
public class ConvertersListResponse extends SuccessfulResponse {
    private List<ConverterTypeDto> converters;
}
