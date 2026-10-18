package ru.akvine.istochnik.controllers.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.ErrorResolvePolicy;

@Data
@Accessors(chain = true)
public class ErrorResolveInfo {
    @NotNull
    private ErrorResolvePolicy policy;
    private String defaultValue;
}
