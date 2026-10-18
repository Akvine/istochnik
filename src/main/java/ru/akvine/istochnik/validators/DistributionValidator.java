package ru.akvine.istochnik.validators;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.controllers.dto.DistributionDto;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.exceptions.InvalidDistributionException;
import ru.akvine.istochnik.services.distribution.DistributionAxis;
import ru.akvine.istochnik.services.distribution.DistributionFactory;

/**
 * Проверяет описание распределения случайных значений колонки
 */
@Component
public class DistributionValidator {

    public List<String> validate(ColumnDto column) {
        ConfigDto config = column.getConfig();
        DistributionDto distribution = config.getDistribution();
        if (distribution == null) {
            return List.of();
        }

        List<String> errorMessages = new ArrayList<>();

        DistributionAxis axis = DistributionAxis.of(column.getType());
        if (axis == null) {
            errorMessages.add(
                    String.format(ErrorMessages.TYPE_NOT_SUPPORTED_ERROR, column.getType(), DistributionAxis.types()));
            return errorMessages;
        }

        // Последовательность с шагом значения не выбирает, поэтому распределение к ней неприменимо
        if (!RangeType.RANDOM.getType().equalsIgnoreCase(config.getRangeType())) {
            errorMessages.add(ErrorMessages.RANGE_TYPE_IS_NOT_RANDOM_ERROR);
        }

        try {
            DistributionFactory.create(distribution, axis, config.getStart(), config.getEnd());
        } catch (InvalidDistributionException exception) {
            errorMessages.addAll(exception.getErrors());
        }

        return errorMessages;
    }

    interface ErrorMessages {
        String TYPE_NOT_SUPPORTED_ERROR = "field [distribution] is not supported for type [%s]. Supported types: %s";
        String RANGE_TYPE_IS_NOT_RANDOM_ERROR = "field [distribution] is used only when field [rangeType] is [random]";
    }
}
