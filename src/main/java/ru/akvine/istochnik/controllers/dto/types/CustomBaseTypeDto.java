package ru.akvine.istochnik.controllers.dto.types;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CustomBaseTypeDto {
    private String customType;
    private String baseType;
}
