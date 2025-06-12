package ru.akvine.istochnik.validators.type;

import org.springframework.stereotype.Component;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.ConverterType;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;
import ru.akvine.istochnik.validators.type.dto.ValidateAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class DoubleBaseTypeValidator implements BaseTypeValidator{
    @Override
    public List<String> validate(String columnName, ValidateAction action) {
        Asserts.isNotNull(columnName);
        Asserts.isNotNull(action);

        int rowsCount = action.getRowsCount();
        List<String> errorMessages = new ArrayList<>();

        Double start = null;
        Double end = null;
        Double step = null;
        RangeType rangeType = null;

        try {
            start = Double.parseDouble(action.getStart());
        } catch (NumberFormatException exception) {
            errorMessages.add(ErrorMessages.START_FIELD_ERROR);
        }

        try {
            end = Double.parseDouble(action.getEnd());
        } catch (NumberFormatException exception) {
            errorMessages.add(ErrorMessages.END_FIELD_ERROR);
        }

        try {
            rangeType = RangeType.from(action.getRangeType());
        } catch (IllegalArgumentException exception) {
            errorMessages.add(ErrorMessages.RANGE_TYPE_IS_BLANK_ERROR);
        }

        if (start != null &&  end != null && start > end) {
            errorMessages.add(IntegerBaseTypeValidator.ErrorMessages.END_LESS_START_ERROR);
        }


        if (rangeType == RangeType.SHIFT) {
            try {
                step = Double.parseDouble(action.getStep());
            } catch (NumberFormatException exception) {
                errorMessages.add(ErrorMessages.STEP_FIELD_ERROR);
            }

            if (start != null &&  end != null && start > end) {
                errorMessages.add(IntegerBaseTypeValidator.ErrorMessages.END_LESS_START_ERROR);
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

        Collection<String> converters = action.getConverters() == null ? List.of() : action.getConverters();
        for (String converterName : converters) {

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
            } catch (UnsupportedTypeGenerationException exception) {
                errorMessages.add(String.format(ErrorMessages.CONVERTER_NOT_SUPPORTED_ERROR, converterName));
            }
        }

        return errorMessages;
    }

    @Override
    public BaseType getBaseType() {
        return BaseType.DOUBLE;
    }

    interface ErrorMessages {
        String START_FIELD_ERROR = "field [start] is not a double";
        String END_FIELD_ERROR = "field [end] is not a double";
        String STEP_FIELD_ERROR = "field [step] is not a double";
        String STEP_FIELD_INVALID_ERROR = "field [step] can't be less than 0";
        String END_LESS_START_ERROR = "field [end] can't be less than field [start]";

        String RANGE_TYPE_IS_BLANK_ERROR = "field [rangeType] can't be blank";
        String INVALID_RANGE_OR_STEP_ERROR = "field [end] or [step] is invalid. Possible generated rows less than size";

        String CONVERTER_NOT_SUPPORTED_ERROR = "converter with name [%s] is not supported by app";
        String CONVERTER_NOT_SUPPORTED_FOR_BASE_TYPE_ERROR = "converter with name [%s] is not supported for type = [%s]";
    }
}
