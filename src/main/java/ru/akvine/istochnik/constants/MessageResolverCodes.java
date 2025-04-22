package ru.akvine.istochnik.constants;

import ru.akvine.istochnik.enums.BaseType;

public final class MessageResolverCodes {
    public interface Types {
        String INTEGER_TYPE_CODE = BaseType.INTEGER.getCode();
        String DOUBLE_TYPE_CODE = BaseType.DOUBLE.getCode();
        String STRING_TYPE_CODE = BaseType.STRING.getCode();
        String BOOLEAN_TYPE_CODE = BaseType.BOOLEAN.getCode();
    }
}
