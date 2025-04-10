package ru.akvine.istochnik.validators.type;

import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.validators.type.dto.ValidateAction;

import java.util.List;

public interface BaseTypeValidator {
    List<String> validate(String columnName, ValidateAction action);

    BaseType getBaseType();
}
