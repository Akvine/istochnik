package ru.akvine.istochnik.services.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.Type;

import java.util.List;

@Data
@Accessors(chain = true)
public class GenerateColumn {
    private String name;
    private Type type;
    private Config config;
    private List<Filter> filters;
}
