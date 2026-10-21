package ru.akvine.istochnik.controllers.dto.converters;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ConverterExampleDto {
    private List<?> arguments;

    // Указывается, только если пример задаёт вероятность явно
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer probability;

    private List<?> values;
    private List<?> results;
}
