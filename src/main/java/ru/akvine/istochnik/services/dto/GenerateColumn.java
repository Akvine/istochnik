package ru.akvine.istochnik.services.dto;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;

import java.util.List;

@Data
@Accessors(chain = true)
public class GenerateColumn {
    private String name;
    @Nullable
    private BaseType baseType;
    @Nullable
    private CustomType customType;
    private Config config;
    private List<Filter> filters;
}
