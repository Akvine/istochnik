package ru.akvine.istochnik.validators.type;

import java.util.List;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.validators.type.dto.ValidateAction;

public interface BaseTypeValidator {
    List<String> validate(String columnName, ValidateAction action);

    BaseType getBaseType();
}
