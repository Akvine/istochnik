package ru.akvine.istochnik.validators.custom;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;
import ru.akvine.istochnik.services.generators.custom.zoneid.ZoneIdGenerator;

@Component
public class ZoneIdCustomTypeValidator implements CustomTypeValidator {

    @Override
    public List<String> validate(ColumnDto column, int rowsCount) {
        ConfigDto config = column.getConfig();
        List<String> errorMessages = new ArrayList<>();

        // Поле rangeType необязательно: без него идентификаторы выбираются случайно
        if (StringUtils.isNotBlank(config.getRangeType())) {
            try {
                RangeType.from(config.getRangeType());
            } catch (UnsupportedTypeGenerationException exception) {
                errorMessages.add(exception.getMessage());
            }
        }

        if (config.isUnique()) {
            // Если колонка может содержать null, то он выступает ещё одним уникальным значением
            int uniqueValuesCount = ZoneIdGenerator.ZONE_IDS.size() + (config.isNotNull() ? 0 : 1);
            if (rowsCount > uniqueValuesCount) {
                errorMessages.add(
                        String.format(ErrorMessages.NOT_ENOUGH_UNIQUE_VALUES_ERROR, rowsCount, uniqueValuesCount));
            }
        }

        return errorMessages;
    }

    @Override
    public CustomType getCustomType() {
        return CustomType.ZONE_ID;
    }

    interface ErrorMessages {
        String NOT_ENOUGH_UNIQUE_VALUES_ERROR =
                "Rows count = [%s] greater than available unique zone ids = [%s]. "
                        + "Disable field \"unique\": \"true\" or reduce rows count";
    }
}
