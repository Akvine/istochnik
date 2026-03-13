package ru.akvine.istochnik.validators.type;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.akvine.compozit.commons.istochnik.ConverterDto;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.ConverterType;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;
import ru.akvine.istochnik.providers.converters.StringConvertersProvider;
import ru.akvine.istochnik.validators.type.dto.ValidateAction;

@Component
@RequiredArgsConstructor
public class StringBaseTypeValidator implements BaseTypeValidator {
    private final StringConvertersProvider stringConvertersProvider;

    @Override
    public List<String> validate(String columnName, ValidateAction action) {
        Asserts.isNotNull(columnName);
        Asserts.isNotNull(action);

        List<String> errorMessages = new ArrayList<>();

        for (ConverterDto converterDto : action.getConverters()) {

            String converterName = converterDto.getName();
            ConverterType converterType;
            try {
                converterType = ConverterType.safeFrom(converterName);

                if (!getBaseType().isSupported(converterType)) {
                    errorMessages.add(String.format(
                            DoubleBaseTypeValidator.ErrorMessages.CONVERTER_NOT_SUPPORTED_FOR_BASE_TYPE_ERROR,
                            converterName,
                            getBaseType().getValue()));
                }

                if (converterDto.getProbability() < 0 || converterDto.getProbability() > 100) {
                    errorMessages.add(String.format(
                            ErrorMessages.CONVERTER_PROBABILITY_ERROR, converterName, converterDto.getProbability()));
                }

                stringConvertersProvider
                        .getConverter(converterType)
                        .validateArgument(mapArguments(converterDto.getArguments()));
            } catch (UnsupportedTypeGenerationException exception) {
                errorMessages.add(String.format(
                        DoubleBaseTypeValidator.ErrorMessages.CONVERTER_NOT_SUPPORTED_ERROR, converterName));
            } catch (IllegalArgumentException argumentException) {
                errorMessages.add(String.format(
                        DoubleBaseTypeValidator.ErrorMessages.CONVERTER_HAS_INVALID_ARGUMENTS,
                        converterName,
                        argumentException.getMessage()));
            }
        }

        return errorMessages;
    }

    interface ErrorMessages {
        String CONVERTER_PROBABILITY_ERROR =
                "for converter with name [%s] invalid probability = [%s]. Can be only between 0 and 100";
    }

    @Override
    public BaseType getBaseType() {
        return BaseType.STRING;
    }

    private String[] mapArguments(Object[] arguments) {
        String[] array = new String[arguments.length];
        for (int i = 0; i < arguments.length; ++i) {
            array[i] = (String) arguments[i];
        }

        return array;
    }
}
