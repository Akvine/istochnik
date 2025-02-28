package ru.akvine.istochnik.validators;

import org.springframework.stereotype.Component;
import ru.akvine.istochnik.constants.ErrorConstants;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.exceptions.validation.ValidationException;

@Component
public class TypeValidator implements Validator<String> {
    @Override
    public void validate(String type) {
        try {
            BaseType.safeFrom(type);
        } catch (RuntimeException exception) {
            throw new ValidationException(
                    ErrorConstants.Validation.TYPE_INVALID_ERROR,
                    exception.getMessage()
            );
        }
    }
}
