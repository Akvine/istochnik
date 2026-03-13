package ru.akvine.istochnik.providers;

import java.util.Map;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.validators.type.BaseTypeValidator;

public record BaseTypeValidatorsProvider(Map<BaseType, BaseTypeValidator> validators) {

    public BaseTypeValidator get(BaseType baseType) {
        Asserts.isNotNull(baseType);

        if (validators.containsKey(baseType)) {
            return validators.get(baseType);
        }

        throw new IllegalArgumentException("Base type validator = [" + baseType + "] is not supported by app");
    }
}
