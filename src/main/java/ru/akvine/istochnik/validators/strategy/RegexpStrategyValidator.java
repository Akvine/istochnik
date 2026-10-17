package ru.akvine.istochnik.validators.strategy;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.istochnik.config.regexp.RegexpProperties;
import ru.akvine.istochnik.controllers.dto.validation.ValidationColumnsInfo;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.exceptions.InvalidRegexpException;
import ru.akvine.istochnik.services.regexp.RegexpAnalyzer;

/**
 * Проверяет выражения до генерации: слишком длинное или сложное выражение иначе обнаружилось бы
 * только при построении автомата, которое может занять минуты и всю память
 */
@Component
@RequiredArgsConstructor
public class RegexpStrategyValidator implements GenerationStrategyValidator {
    private final RegexpProperties regexpProperties;
    private final RegexpAnalyzer regexpAnalyzer;

    @Override
    public void validate(ValidationColumnsInfo validationColumnsInfo, ColumnDto column, int rowsCount) {
        String columnName = column.getName();

        ConfigDto config = column.getConfig();
        if (config == null) {
            validationColumnsInfo.put(columnName, ErrorMessages.CONFIG_IS_NULL_ERROR);
            return;
        }

        Set<String> regexps = config.getRegexps();
        if (CollectionUtils.isEmpty(regexps)) {
            validationColumnsInfo.put(columnName, ErrorMessages.REGEXPS_ARE_EMPTY_ERROR);
            return;
        }

        if (regexps.size() > regexpProperties.getMaxCount()) {
            validationColumnsInfo.put(
                    columnName, String.format(ErrorMessages.REGEXPS_COUNT_ERROR, regexpProperties.getMaxCount()));
            return;
        }

        for (String regexp : regexps) {
            if (regexp == null) {
                validationColumnsInfo.put(columnName, ErrorMessages.REGEXP_IS_NULL_ERROR);
                continue;
            }

            try {
                regexpAnalyzer.verify(regexp);
            } catch (InvalidRegexpException exception) {
                validationColumnsInfo.put(columnName, exception.getMessage());
            }
        }
    }

    @Override
    public GenerationStrategy getStrategy() {
        return GenerationStrategy.REGEXP;
    }

    interface ErrorMessages {
        String CONFIG_IS_NULL_ERROR = "field [config] can't be null";
        String REGEXPS_ARE_EMPTY_ERROR = "field [regexps] can't be empty";
        String REGEXPS_COUNT_ERROR = "regexps count can't be more than max = [%s]";
        String REGEXP_IS_NULL_ERROR = "regexp can't be null";
    }
}
