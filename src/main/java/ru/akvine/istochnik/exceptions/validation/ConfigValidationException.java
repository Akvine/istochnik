package ru.akvine.istochnik.exceptions.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.akvine.istochnik.controllers.dto.validation.ValidationColumnsInfo;

@Getter
@RequiredArgsConstructor
public class ConfigValidationException extends RuntimeException {
    private final String generalInfo;
    private final ValidationColumnsInfo validationColumnsInfo;
}
