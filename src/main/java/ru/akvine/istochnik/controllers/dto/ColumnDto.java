package ru.akvine.istochnik.controllers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.controllers.dto.converters.*;

import java.util.List;

@Data
@Accessors(chain = true)
public class ColumnDto {
    private @NotBlank String name;
    private @NotBlank String type;
    private @NotBlank String generationStrategy;
    private ConfigDto config;
    private boolean convertToString;
    private List<ConverterDto> postConverters;
    private List<ConverterDto> converters;
    private @NotNull ErrorResolveInfo errorResolveInfo = new ErrorResolveInfo();
}
