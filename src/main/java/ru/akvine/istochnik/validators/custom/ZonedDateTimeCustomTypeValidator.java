package ru.akvine.istochnik.validators.custom;

import jakarta.annotation.Nullable;
import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;
import ru.akvine.istochnik.utils.ZonedDateTimeUtils;

@Component
public class ZonedDateTimeCustomTypeValidator implements CustomTypeValidator {

    @Override
    public List<String> validate(ColumnDto column, int rowsCount) {
        ConfigDto config = column.getConfig();
        List<String> errorMessages = new ArrayList<>();

        ZonedDateTime start = parse(config.getStart());
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
        return CustomType.ZONED_DATETIME;
    }

    private void validateRandomRange(
            ConfigDto config, @Nullable ZonedDateTime start, int rowsCount, List<String> errorMessages) {
        ZonedDateTime end = parse(config.getEnd());
        if (end == null) {
            errorMessages.add(ErrorMessages.END_FIELD_ERROR);
            return;
        }

        if (start == null) {
            return;
        }

        // Значения генерируются с точностью до секунды
        long uniqueValuesCount = end.toEpochSecond() - start.toEpochSecond();
        if (uniqueValuesCount <= 0) {
            errorMessages.add(ErrorMessages.END_NOT_GREATER_START_ERROR);
            return;
        }

        if (config.isUnique()) {
            // Если колонка может содержать null, то он выступает ещё одним уникальным значением
            if (!config.isNotNull()) {
                uniqueValuesCount++;
            }

            if (rowsCount > uniqueValuesCount) {
                errorMessages.add(
                        String.format(ErrorMessages.NOT_ENOUGH_UNIQUE_VALUES_ERROR, rowsCount, uniqueValuesCount));
            }
        }
    }

    private void validateShiftRange(
            ConfigDto config, @Nullable ZonedDateTime start, int rowsCount, List<String> errorMessages) {
        // Поле step для shift необязательно: по умолчанию шаг равен одной секунде
        Long step = 1L;
        if (StringUtils.isNotBlank(config.getStep())) {
            try {
                step = Long.parseLong(config.getStep().trim());
            } catch (NumberFormatException exception) {
                errorMessages.add(ErrorMessages.STEP_FIELD_ERROR);
                step = null;
            }

            if (step != null && step <= 0) {
                errorMessages.add(ErrorMessages.STEP_FIELD_INVALID_ERROR);
                step = null;
            }
        }

        // Поле end для shift необязательно: без него генерируется ровно size значений от start с шагом step
        if (StringUtils.isBlank(config.getEnd())) {
            return;
        }

        ZonedDateTime end = parse(config.getEnd());
        if (end == null) {
            errorMessages.add(ErrorMessages.END_FIELD_ERROR);
            return;
        }

        if (start == null) {
            return;
        }

        long seconds = end.toEpochSecond() - start.toEpochSecond();
        if (seconds < 0) {
            errorMessages.add(ErrorMessages.END_LESS_START_ERROR);
            return;
        }

        // Правая граница включается в диапазон
        if (step != null && seconds / step + 1 < rowsCount) {
            errorMessages.add(ErrorMessages.INVALID_RANGE_OR_STEP_ERROR);
        }
    }

    @Nullable
    private ZonedDateTime parse(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        try {
            return ZonedDateTimeUtils.parse(value);
        } catch (DateTimeException exception) {
            return null;
        }
    }

    interface ErrorMessages {
        String START_FIELD_ERROR =
                "field [start] is not a zoned datetime. Expected format: " + ZonedDateTimeUtils.FORMAT_EXAMPLES;
        String END_FIELD_ERROR =
                "field [end] is not a zoned datetime. Expected format: " + ZonedDateTimeUtils.FORMAT_EXAMPLES;
        String STEP_FIELD_ERROR = "field [step] is not an integer count of seconds";
        String STEP_FIELD_INVALID_ERROR = "field [step] must be greater than 0";
        String END_NOT_GREATER_START_ERROR = "field [end] must be greater than field [start]";
        String END_LESS_START_ERROR = "field [end] can't be less than field [start]";

        String RANGE_TYPE_IS_BLANK_ERROR = "field [rangeType] can't be blank";
        String INVALID_RANGE_OR_STEP_ERROR = "field [end] or [step] is invalid. Possible generated rows less than size";
        String NOT_ENOUGH_UNIQUE_VALUES_ERROR =
                "Rows count = [%s] greater than available unique values = [%s]. "
                        + "Disable field \"unique\": \"true\", increase range or reduce rows count";
    }
}
