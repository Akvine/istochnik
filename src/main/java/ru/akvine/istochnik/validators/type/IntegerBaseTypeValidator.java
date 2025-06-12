package ru.akvine.istochnik.validators.type;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.akvine.compozit.commons.istochnik.ConverterDto;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.ConverterType;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;
import ru.akvine.istochnik.providers.converters.IntegerConvertersProvider;
import ru.akvine.istochnik.validators.type.dto.ValidateAction;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class IntegerBaseTypeValidator implements BaseTypeValidator {
    private final IntegerConvertersProvider integerConvertersProvider;

    @Override
    public List<String> validate(String columnName, ValidateAction action) {
        Asserts.isNotNull(columnName);
        Asserts.isNotNull(action);

        List<String> errorMessages = new ArrayList<>();
        int rowsCount = action.getRowsCount();

        Integer start = null;
        Integer end = null;
        Integer step = null;
        RangeType rangeType = null;

        try {
            start = Integer.parseInt(action.getStart());
        } catch (NumberFormatException exception) {
            errorMessages.add(ErrorMessages.START_FIELD_ERROR);
        }

        try {
            end = Integer.parseInt(action.getEnd());
        } catch (NumberFormatException exception) {
            errorMessages.add(ErrorMessages.END_FIELD_ERROR);
        }

        try {
            rangeType = RangeType.from(action.getRangeType());
        } catch (IllegalArgumentException exception) {
            errorMessages.add(ErrorMessages.RANGE_TYPE_IS_BLANK_ERROR);
        } catch (UnsupportedTypeGenerationException exception) {
            errorMessages.add(exception.getMessage());
        }

        if (start != null &&  end != null && start > end) {
            errorMessages.add(ErrorMessages.END_LESS_START_ERROR);
        }

        if (rangeType == RangeType.SHIFT) {
            try {
                step = Integer.parseInt(action.getStep());
            } catch (NumberFormatException exception) {
                errorMessages.add(ErrorMessages.STEP_FIELD_ERROR);
            }


            if (step != null && step < 0) {
                errorMessages.add(ErrorMessages.STEP_FIELD_INVALID_ERROR);
            }

            if (step != null && start != null && end != null) {
                if ((end - start) / step < rowsCount) {
                    errorMessages.add(ErrorMessages.INVALID_RANGE_OR_STEP_ERROR);
                }
            }
        }


        for (ConverterDto converterDto : action.getConverters()) {

            String converterName = converterDto.getName();
            ConverterType converterType;
            try {
                converterType = ConverterType.safeFrom(converterName);

                if (!getBaseType().isSupported(converterType)) {
                    errorMessages.add(String.format(
                            ErrorMessages.CONVERTER_NOT_SUPPORTED_FOR_BASE_TYPE_ERROR,
                            converterName,
                            getBaseType().getValue()
                    ));
                }

                integerConvertersProvider.getConverter(converterType).validateArgument(
                        mapArguments(converterDto.getArguments()));
            } catch (UnsupportedTypeGenerationException exception) {
                errorMessages.add(String.format(ErrorMessages.CONVERTER_NOT_SUPPORTED_ERROR, converterName));
            } catch (IllegalArgumentException argumentException) {
                errorMessages.add(
                        String.format(DoubleBaseTypeValidator.ErrorMessages.CONVERTER_HAS_INVALID_ARGUMENTS,
                                converterName,
                                argumentException.getMessage()));
            }
        }

        return errorMessages;
    }

    @Override
    public BaseType getBaseType() {
        return BaseType.INTEGER;
    }

    interface ErrorMessages {
        String START_FIELD_ERROR = "field [start] is not an integer";
        String END_FIELD_ERROR = "field [end] is not an integer";
        String STEP_FIELD_ERROR = "field [step] is not an integer";
        String STEP_FIELD_INVALID_ERROR = "field [step] can't be less than 0";
        String END_LESS_START_ERROR = "field [end] can't be less than field [start]";

        String RANGE_TYPE_IS_BLANK_ERROR = "field [rangeType] can't be blank";
        String INVALID_RANGE_OR_STEP_ERROR = "field [end] or [step] is invalid. Possible generated rows less than size";

        String CONVERTER_NOT_SUPPORTED_ERROR = "converter with name [%s] is not supported by app";
        String CONVERTER_NOT_SUPPORTED_FOR_BASE_TYPE_ERROR = "converter with name [%s] is not supported for type = [%s]";
    }

    // TODO: дублирование кода тут и в IntegerConverterService
    private Double[] mapArguments(Object[] arguments) {
        Double[] array = new Double[arguments.length];
        for (int i = 0; i < arguments.length; ++i) {
            array[i] = (Double) arguments[i];
        }

        return array;
    }
}
