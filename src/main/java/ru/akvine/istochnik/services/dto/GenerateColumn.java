package ru.akvine.istochnik.services.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.Type;

@Data
@Accessors(chain = true)
public class GenerateColumn {
    private String name;
    private Type type;
    private Config config;
}
