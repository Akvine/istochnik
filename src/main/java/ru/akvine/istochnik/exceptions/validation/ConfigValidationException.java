package ru.akvine.istochnik.exceptions.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.akvine.istochnik.controllers.dto.validation.GeneralInfo;
import ru.akvine.istochnik.controllers.dto.validation.ValidationColumnsInfo;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ConfigValidationException extends RuntimeException {
    private final String generalInfo;
    private final ValidationColumnsInfo validationColumnsInfo;
}
