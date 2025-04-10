package ru.akvine.istochnik.controllers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ColumnDto {
    @NotBlank
    private String name;

    @NotBlank
    private String type;

    private ConfigDto config;

    private List<FilterDto> filters;
}
