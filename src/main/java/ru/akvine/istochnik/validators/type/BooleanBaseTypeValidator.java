package ru.akvine.istochnik.validators.type;

import java.util.List;
import org.springframework.stereotype.Component;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.validators.type.dto.ValidateAction;

@Component
public class BooleanBaseTypeValidator implements BaseTypeValidator {
    @Override
    public List<String> validate(String columnName, ValidateAction action) {
        return List.of();
    }

    @Override
    public BaseType getBaseType() {
        return BaseType.BOOLEAN;
    }
}
