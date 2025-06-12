package ru.akvine.istochnik.controllers.converters;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.ConverterDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.enums.*;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.dto.GenerateColumn;
import ru.akvine.istochnik.services.dto.GenerateData;

import java.util.ArrayList;
import java.util.List;

@Component
public class GeneratorConverter {
    public GenerateData convertToGenerateData(GenerateTableRequest request) {
        Asserts.isNotNull(request);

        int size = request.getSize();
        List<GenerateColumn> generateColumns = new ArrayList<>();
        for (ColumnDto column : request.getColumns()) {

            GenerationStrategy strategy = GenerationStrategy.from(column.getGenerationStrategy());
            CustomType customType = null;
            BaseType baseType = null;
            if (strategy == GenerationStrategy.ALGORITHM) {
                baseType = BaseType.from(column.getType());
                if (baseType == null) {
                    customType = CustomType.safeFrom(column.getType());
                }
            }

            generateColumns.add(new GenerateColumn()
                    .setName(column.getName())
                    .setBaseType(baseType)
                    .setCustomType(customType)
                    .setConfig(buildConfig(size, column.getConfig()))
                    .setGenerationStrategy(strategy)
                    .setConvertToString(column.isConvertToString())
                    .setPostConverters(CollectionUtils.isEmpty(column.getPostConverters()) ?
                            List.of() : column.getPostConverters().stream().map(this::buildConverter).toList())
                    .setConverters(CollectionUtils.isEmpty(column.getConverters()) ?
                            List.of() : column.getConverters().stream().map(this::buildConverter).toList()));
        }

        return new GenerateData()
                .setSize(size)
                .setGenerateColumns(generateColumns);
    }

    public ResponseEntity<?> convertToResponse(byte[] file, String reportType) {
        Asserts.isNotNull(file);
        Asserts.isNotNull(reportType);

        String mimeType;
        switch (reportType.toLowerCase()) {
            case "csv": {
                mimeType = "application/csv";
                break;
            }
            case "xlsx": {
                mimeType = "application/xlsx";
                break;
            }
            default:
                throw new UnsupportedOperationException("Mime type = [" + reportType + "] is not supported!");
        }

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, mimeType)
                .body(file);
    }

    private Converter buildConverter(ConverterDto converterDto) {
        return new Converter()
                .setName(ConverterType.safeFrom(converterDto.getName()))
                .setArguments(converterDto.getArguments());
    }

    private Config buildConfig(int size, ConfigDto configDto) {
        return new Config()
                .setSize(size)
                .setNotNull(configDto.isNotNull())
                .setUnique(configDto.isUnique())
                .setRangeType(StringUtils.isBlank(configDto.getRangeType()) ?
                        null : RangeType.valueOf(configDto.getRangeType()))
                .setStart(configDto.getStart())
                .setEnd(configDto.getEnd())
                .setStep(configDto.getStep())
                .setLength(configDto.getLength())
                .setValid(configDto.isValid())
                .setDictionaries(configDto.getDictionaries())
                .setConstant(configDto.getConstant())
                .setRegexps(configDto.getRegexps());
    }
}
