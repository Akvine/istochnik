package ru.akvine.istochnik.validators.custom;

import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;

@Component
public class DecimalCustomTypeValidator implements CustomTypeValidator {
    // Ограничения защищают от значений вида 1E+1000000000, разворачивание которых исчерпает память
    private static final int MAX_VALUE_LENGTH = 100;
    private static final int MAX_DIGITS = 38;

    @Override
    public List<String> validate(ColumnDto column, int rowsCount) {
        ConfigDto config = column.getConfig();
        List<String> errorMessages = new ArrayList<>();

        BigDecimal start = parse(config.getStart());
        if (start == null) {
            errorMessages.add(ErrorMessages.START_FIELD_ERROR);
        }

        RangeType rangeType = null;
        try {
            rangeType = RangeType.from(config.getRangeType());
        } catch (UnsupportedTypeGenerationException exception) {
            errorMessages.add(exception.getMessage());
        } catch (IllegalArgumentException exception) {
            errorMessages.add(ErrorMessages.RANGE_TYPE_IS_BLANK_ERROR);
        }

        if (rangeType == RangeType.SHIFT) {
            validateShiftRange(config, start, rowsCount, errorMessages);
        } else if (rangeType == RangeType.RANDOM) {
            validateRandomRange(config, start, rowsCount, errorMessages);
        }

        return errorMessages;
    }

    @Override
    public CustomType getCustomType() {
        return CustomType.DECIMAL;
    }

    private void validateRandomRange(
            ConfigDto config, @Nullable BigDecimal start, int rowsCount, List<String> errorMessages) {
        BigDecimal end = parse(config.getEnd());
        if (end == null) {
            errorMessages.add(ErrorMessages.END_FIELD_ERROR);
            return;
        }

        if (start == null) {
            return;
        }

        if (start.compareTo(end) >= 0) {
            errorMessages.add(ErrorMessages.END_NOT_GREATER_START_ERROR);
            return;
        }

        if (config.isUnique()) {
            int scale = Math.max(Math.max(start.scale(), end.scale()), 0);
            BigInteger uniqueValuesCount =
                    end.subtract(start).movePointRight(scale).toBigInteger();
            // Если колонка может содержать null, то он выступает ещё одним уникальным значением
            if (!config.isNotNull()) {
                uniqueValuesCount = uniqueValuesCount.add(BigInteger.ONE);
            }

            if (BigInteger.valueOf(rowsCount).compareTo(uniqueValuesCount) > 0) {
                errorMessages.add(
                        String.format(ErrorMessages.NOT_ENOUGH_UNIQUE_VALUES_ERROR, rowsCount, uniqueValuesCount));
            }
        }
    }

    private void validateShiftRange(
            ConfigDto config, @Nullable BigDecimal start, int rowsCount, List<String> errorMessages) {
        BigDecimal step = parse(config.getStep());
        if (step == null) {
            errorMessages.add(ErrorMessages.STEP_FIELD_ERROR);
        } else if (step.signum() <= 0) {
            errorMessages.add(ErrorMessages.STEP_FIELD_INVALID_ERROR);
            step = null;
        }

        // Поле end для shift необязательно: без него генерируется ровно size значений от start с шагом step
        if (StringUtils.isBlank(config.getEnd())) {
            return;
        }

        BigDecimal end = parse(config.getEnd());
        if (end == null) {
            errorMessages.add(ErrorMessages.END_FIELD_ERROR);
            return;
        }

        if (start == null) {
            return;
        }

        if (start.compareTo(end) >= 0) {
            errorMessages.add(ErrorMessages.END_NOT_GREATER_START_ERROR);
            return;
        }

        if (step != null) {
            BigDecimal possibleValuesCount = end.subtract(start).divide(step, 0, RoundingMode.CEILING);
            if (possibleValuesCount.compareTo(BigDecimal.valueOf(rowsCount)) < 0) {
                errorMessages.add(ErrorMessages.INVALID_RANGE_OR_STEP_ERROR);
            }
        }
    }

    @Nullable
    private BigDecimal parse(String value) {
        if (StringUtils.isBlank(value) || value.length() > MAX_VALUE_LENGTH) {
            return null;
        }

        BigDecimal parsed;
        try {
            parsed = new BigDecimal(value.trim());
        } catch (NumberFormatException exception) {
            return null;
        }

        long integerDigits = (long) parsed.precision() - parsed.scale();
        if (integerDigits > MAX_DIGITS || parsed.scale() > MAX_DIGITS) {
            return null;
        }

        return parsed;
    }

    interface ErrorMessages {
        String START_FIELD_ERROR = "field [start] is not a decimal with no more than 38 integer and 38 fraction digits";
        String END_FIELD_ERROR = "field [end] is not a decimal with no more than 38 integer and 38 fraction digits";
        String STEP_FIELD_ERROR = "field [step] is not a decimal with no more than 38 integer and 38 fraction digits";
        String STEP_FIELD_INVALID_ERROR = "field [step] must be greater than 0";
        String END_NOT_GREATER_START_ERROR = "field [end] must be greater than field [start]";

        String RANGE_TYPE_IS_BLANK_ERROR = "field [rangeType] can't be blank";
        String INVALID_RANGE_OR_STEP_ERROR = "field [end] or [step] is invalid. Possible generated rows less than size";
        String NOT_ENOUGH_UNIQUE_VALUES_ERROR =
                "Rows count = [%s] greater than available unique values = [%s]. "
                        + "Disable field \"unique\": \"true\", increase range or reduce rows count";
    }
}
