package ru.akvine.istochnik.api.common.providers;

import java.util.Map;
import ru.akvine.istochnik.api.common.converters.TypeConverter;
import ru.akvine.istochnik.enums.BaseType;

public record TypeConvertersProvider(Map<BaseType, TypeConverter<?>> typeConverters) {
    public TypeConverter<?> get(BaseType baseType) {
        if (typeConverters.containsKey(baseType)) {
            return typeConverters.get(baseType);
        }

        throw new IllegalArgumentException(
                "Type converter for base type = [" + baseType + "] is not supported by app!");
    }
}
