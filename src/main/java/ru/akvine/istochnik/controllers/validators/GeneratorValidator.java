package ru.akvine.istochnik.controllers.validators;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.controllers.dto.validation.ValidationColumnsInfo;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;
import ru.akvine.istochnik.exceptions.validation.ConfigValidationException;
import ru.akvine.istochnik.providers.GenerationStrategyValidatorsProvider;
import ru.akvine.istochnik.validators.strategy.GenerationStrategyValidator;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GeneratorValidator {
    private final GenerationStrategyValidatorsProvider generationStrategyValidatorsProvider;

    @Value("${max.dictionaries.per.column}")
    private int maxDictionariesPerColumn;

    @Value("${max.dictionary.elements.count}")
    private int maxDictionaryElementsCount;

    @Value("${max.generation.rows.size}")
    private int maxGenerationRowsSize;

    public void verifyGenerateTableRequest(GenerateTableRequest request) {
        Asserts.isNotNull(request);

        int rowsCount = request.getSize();
        List<ColumnDto> columns = request.getColumns();

        StringBuilder sb = new StringBuilder();
        if (rowsCount < 0) {
            sb.append("size can't be less than 0");
        }

        if (rowsCount > maxGenerationRowsSize) {
            String message = String.format(
                    "Can't generate rows size = [%s] more than max = [%s]", rowsCount, maxGenerationRowsSize);
            sb.append(message);
        }

        try {
            FileType.from(request.getFileType());
        } catch (RuntimeException exception) {
            if (StringUtils.isNotBlank(sb.toString())) {
                sb.append(". ");
            }
            sb.append(exception.getMessage());
        }

        ValidationColumnsInfo validationColumnsInfo = new ValidationColumnsInfo();
        for (ColumnDto column : columns) {
            String columnName = column.getName();

            GenerationStrategy strategy;
            try {
                strategy = GenerationStrategy.from(column.getGenerationStrategy());
                GenerationStrategyValidator validator = generationStrategyValidatorsProvider.getByType(strategy);
                if (validator != null) {
                    validator.validate(validationColumnsInfo, column, rowsCount);
                }

            } catch (UnsupportedTypeGenerationException | IllegalArgumentException exception) {
                validationColumnsInfo.put(columnName, exception.getMessage());
            }
        }

        if (StringUtils.isNotBlank(sb.toString())
                || !validationColumnsInfo.getColumnNamesPerErrorMessages().isEmpty()) {
            throw new ConfigValidationException(sb.toString(), validationColumnsInfo);
        }
    }
}
