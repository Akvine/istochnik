package ru.akvine.istochnik.services.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Column {
    private String name;
}
