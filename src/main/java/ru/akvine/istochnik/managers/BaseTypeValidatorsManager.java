package ru.akvine.istochnik.managers;

import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.validators.type.BaseTypeValidator;

import java.util.Map;

public record BaseTypeValidatorsManager(Map<BaseType, BaseTypeValidator> validators) {

    public BaseTypeValidator get(BaseType baseType) {
        Asserts.isNotNull(baseType);

        if (validators.containsKey(baseType)) {
            return validators.get(baseType);
        }

        throw new IllegalArgumentException("Base type validator = [" + baseType + "] is not supported by app");
    }
}
