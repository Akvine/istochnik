package ru.akvine.istochnik.validators.strategy;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.config.regexp.RegexpProperties;
import ru.akvine.istochnik.controllers.dto.validation.ValidationColumnsInfo;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.exceptions.InvalidRegexpException;
import ru.akvine.istochnik.services.regexp.RegexpAnalyzer;

/**
 * Проверяет выражения до генерации: слишком длинное или сложное выражение иначе обнаружилось бы
 * только при построении автомата, которое может занять минуты и всю память
 */
@Component
@RequiredArgsConstructor
public class RegexpStrategyValidator implements GenerationStrategyValidator {
    // Длина выражения в сообщении об ошибке вместе с многоточием
    private static final int SHOWN_REGEXP_LENGTH = 53;

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

        // Для unique выражение не только проверяется, но и считается, сколько разных строк оно задаёт
        Map<String, Long> stringsCounts = new LinkedHashMap<>();
        boolean allValid = true;
        for (String regexp : regexps) {
            if (regexp == null) {
                validationColumnsInfo.put(columnName, ErrorMessages.REGEXP_IS_NULL_ERROR);
                allValid = false;
                continue;
            }

            try {
                if (config.isUnique()) {
                    stringsCounts.put(regexp, regexpAnalyzer.countStrings(regexp));
                } else {
                    regexpAnalyzer.verify(regexp);
                }
            } catch (InvalidRegexpException exception) {
                validationColumnsInfo.put(columnName, exception.getMessage());
                allValid = false;
            }
        }

        if (config.isUnique() && allValid) {
            validateUniqueValuesCount(validationColumnsInfo, column, rowsCount, stringsCounts);
        }
    }

    /**
     * Разных строк, которые задают выражения, должно хватить на все строки колонки. Одна строка может подходить
     * под несколько выражений, поэтому их сумма - оценка сверху: здесь находится только заведомая нехватка, а
     * остальная обнаружится при генерации
     */
    private void validateUniqueValuesCount(
            ValidationColumnsInfo validationColumnsInfo,
            ColumnDto column,
            int rowsCount,
            Map<String, Long> stringsCounts) {
        String columnName = column.getName();
        // Если колонка допускает null, он - ещё одно уникальное значение
        long nullValues = column.getConfig().isNotNull() ? 0 : 1;

        long uniqueValues = nullValues;
        for (long count : stringsCounts.values()) {
            uniqueValues = count > Long.MAX_VALUE - uniqueValues ? Long.MAX_VALUE : uniqueValues + count;
        }
        if (uniqueValues < rowsCount) {
            validationColumnsInfo.put(
                    columnName, String.format(ErrorMessages.UNIQUE_VALUES_ERROR, rowsCount, uniqueValues));
            return;
        }

        // По порядку выражения чередуются, и каждое даёт значения не меньше чем для каждой n-й строки
        if (!RangeType.RANDOM.getType().equalsIgnoreCase(column.getConfig().getRangeType())) {
            long rowsPerRegexp = rowsCount / stringsCounts.size();
            stringsCounts.forEach((regexp, count) -> {
                if (count < rowsPerRegexp - nullValues) {
                    validationColumnsInfo.put(
                            columnName,
                            String.format(
                                    ErrorMessages.REGEXP_UNIQUE_VALUES_ERROR,
                                    StringUtils.abbreviate(regexp, SHOWN_REGEXP_LENGTH),
                                    count,
                                    rowsPerRegexp));
                }
            });
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
        String UNIQUE_VALUES_ERROR = "Rows count = [%s] greater than available unique values of regexps = [%s]. "
                + "Disable field \"unique\": \"true\", make regexps broader or reduce rows count";
        String REGEXP_UNIQUE_VALUES_ERROR = "regexp [%s] sets only [%s] unique values, but with rangeType = [shift] "
                + "it is used for at least [%s] rows. Disable field \"unique\": \"true\", use rangeType = [random], "
                + "make the regexp broader or reduce rows count";
    }
}
