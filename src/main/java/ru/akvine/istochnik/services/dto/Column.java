package ru.akvine.istochnik.services.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class Column {
    private String name;
    private List<?> values;
}
