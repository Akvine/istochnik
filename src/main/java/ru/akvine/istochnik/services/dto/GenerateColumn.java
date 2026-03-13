package ru.akvine.istochnik.services.dto;

import jakarta.annotation.Nullable;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.compozit.commons.istochnik.ErrorResolveInfo;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.GenerationStrategy;

@Data
@Accessors(chain = true)
public class GenerateColumn {
    private String name;

    @Nullable
    private BaseType baseType;

    @Nullable
    private CustomType customType;

    private Config config;
    private List<Converter> converters;
    private boolean convertToString;
    private List<Converter> postConverters;
    private GenerationStrategy generationStrategy;
    private ErrorResolveInfo errorResolveInfo;
}
