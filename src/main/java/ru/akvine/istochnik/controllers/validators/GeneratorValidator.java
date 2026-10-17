package ru.akvine.istochnik.controllers.validators;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.SpreadsheetVersion;
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
import ru.akvine.istochnik.services.limits.GenerationLimits;
import ru.akvine.istochnik.services.limits.GenerationLimitsService;
import ru.akvine.istochnik.validators.strategy.ExpressionStrategyValidator;
import ru.akvine.istochnik.validators.strategy.GenerationStrategyValidator;

@Component
@RequiredArgsConstructor
public class GeneratorValidator {
    private final GenerationStrategyValidatorsProvider generationStrategyValidatorsProvider;
    private final ExpressionStrategyValidator expressionStrategyValidator;
    private final GenerationLimitsService generationLimitsService;

    public void verifyGenerateTableRequest(GenerateTableRequest request) {
        Asserts.isNotNull(request);

        GenerationLimits limits = generationLimitsService.getLimits();
        int maxGenerationRowsSize = limits.maxRowsSize();
        int maxGenerationColumnsSize = limits.maxColumnsSize();
        // Ячейка XLSX занимает в памяти больше, поэтому лимит ячеек для этого формата свой
        boolean xlsx = FileType.XLSX.name().equalsIgnoreCase(request.getFileType());
        long maxGenerationCellsSize = xlsx ? limits.maxXlsxCellsSize() : limits.maxCellsSize();

        int rowsCount = request.getSize();
        List<ColumnDto> columns = request.getColumns();

        List<String> generalErrors = new ArrayList<>();
        if (rowsCount < 0) {
            generalErrors.add("size can't be less than 0");
        }

        if (rowsCount > maxGenerationRowsSize) {
            String message = String.format(
                    "Can't generate rows size = [%s] more than max = [%s]", rowsCount, maxGenerationRowsSize);
            generalErrors.add(message);
        }

        if (CollectionUtils.isEmpty(columns)) {
            generalErrors.add("Field 'columns' can't be empty");
        } else if (columns.size() > maxGenerationColumnsSize) {
            String message = String.format(
                    "Can't generate columns count = [%s] more than max = [%s]",
                    columns.size(), maxGenerationColumnsSize);
            generalErrors.add(message);
        } else if (rowsCount <= maxGenerationRowsSize) {
            // Таблица целиком лежит в памяти, поэтому ограничены не только строки и колонки, но и их произведение
            long cellsCount = (long) rowsCount * columns.size();
            if (cellsCount > maxGenerationCellsSize) {
                String message = String.format(
                        "Can't generate cells count = [%s] (rows * columns) more than max = [%s]%s",
                        cellsCount, maxGenerationCellsSize, xlsx ? " for file type = XLSX" : "");
                generalErrors.add(message);
            }
        }

        FileType fileType = null;
        try {
            fileType = FileType.from(request.getFileType());
        } catch (RuntimeException exception) {
            generalErrors.add(exception.getMessage());
        }

        // TODO: добавить валидацию для каждого из fileType через providers
        if (FileType.SQL == fileType && StringUtils.isBlank(request.getTableName())) {
            generalErrors.add("Field 'tableName' can't be blank for file type = SQL");
        }

        // Лист XLSX вмещает ограниченное число строк и колонок, а первая строка занята заголовком.
        // Рассчитанные по ресурсам машины лимиты могут быть больше
        if (FileType.XLSX == fileType) {
            int maxXlsxRows = SpreadsheetVersion.EXCEL2007.getLastRowIndex();
            if (rowsCount > maxXlsxRows) {
                generalErrors.add(String.format(
                        "Can't generate rows size = [%s] more than max = [%s] for file type = XLSX",
                        rowsCount, maxXlsxRows));
            }

            int maxXlsxColumns = SpreadsheetVersion.EXCEL2007.getMaxColumns();
            if (columns != null && columns.size() > maxXlsxColumns) {
                generalErrors.add(String.format(
                        "Can't generate columns count = [%s] more than max = [%s] for file type = XLSX",
                        columns.size(), maxXlsxColumns));
            }
        }

        ValidationColumnsInfo validationColumnsInfo = new ValidationColumnsInfo();
        // Колонки сверх лимита не проверяются: запрос уже отклонён, а проверка каждой колонки стоит ресурсов
        if (CollectionUtils.isNotEmpty(columns) && columns.size() <= maxGenerationColumnsSize) {
            validateColumns(columns, rowsCount, validationColumnsInfo);
        }

        if (!generalErrors.isEmpty()
                || !validationColumnsInfo.getColumnNamesPerErrorMessages().isEmpty()) {
            throw new ConfigValidationException(String.join(". ", generalErrors), validationColumnsInfo);
        }
    }

    private void validateColumns(List<ColumnDto> columns, int rowsCount, ValidationColumnsInfo validationColumnsInfo) {
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

        // Ссылки вычисляемых колонок проверяются по всему запросу, а не по одной колонке
        expressionStrategyValidator.validateDependencies(validationColumnsInfo, columns);
    }
}
