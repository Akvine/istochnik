package ru.akvine.istochnik.controllers.dto.validation;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GeneralInfo {
    private String message;
}
