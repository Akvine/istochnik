package ru.akvine.istochnik.services.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class GenerateData {
    private List<GenerateColumn> generateColumns;
    private int size;
}
