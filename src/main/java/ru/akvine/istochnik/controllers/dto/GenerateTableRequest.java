package ru.akvine.istochnik.controllers.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class GenerateTableRequest {
    private List<ColumnDto> columns;
    private int size;
}
