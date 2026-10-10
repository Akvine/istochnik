package ru.akvine.istochnik.providers;

import jakarta.annotation.Nullable;
import java.util.Map;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.validators.custom.CustomTypeValidator;

public record CustomTypeValidatorsProvider(Map<CustomType, CustomTypeValidator> validators) {
    // Собственная проверка есть не у каждого пользовательского типа
    @Nullable
    public CustomTypeValidator getByType(CustomType type) {
        return validators.get(type);
    }
}
