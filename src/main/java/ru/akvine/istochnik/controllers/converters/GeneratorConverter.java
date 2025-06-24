package ru.akvine.istochnik.controllers.converters;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
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

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.random.RandomGenerator;

@Component
@Slf4j
public class GeneratorConverter {

    @Value("${test.randomizer.enabled}")
    private boolean testRandomizerEnabled;
    @Value("${test.randomizer.seed:#{null}}")
    private Integer seed;

    private static final RandomGenerator SECURE_RANDOM_GENERATOR = new SecureRandom();

    @PostConstruct
    public void init() {
        if (testRandomizerEnabled) {
            log.info("Test randomizer implementation by {} class is using! Use only for TEST environment!!!",
                    Random.class.getSimpleName());
        }
    }

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

            generateColumns.add(
                    new GenerateColumn()
                            .setName(column.getName())
                            .setBaseType(baseType)
                            .setCustomType(customType)
                            .setConfig(buildConfig(size, column.getConfig()))
                            .setGenerationStrategy(strategy)
                            .setErrorResolveInfo(column.getErrorResolveInfo())
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
        RandomGenerator randomGenerator;
        if (testRandomizerEnabled) {
            if (seed == null) {
                randomGenerator = new Random();
            } else {
                randomGenerator = new Random(seed);
            }
        } else {
            randomGenerator = SECURE_RANDOM_GENERATOR;
        }

        return new Config()
                .setSize(size)
                .setNotNull(configDto.isNotNull())
                .setUnique(configDto.isUnique())
                .setRangeType(StringUtils.isBlank(configDto.getRangeType()) ?
                        null : RangeType.from(configDto.getRangeType()))
                .setStart(configDto.getStart())
                .setEnd(configDto.getEnd())
                .setStep(configDto.getStep())
                .setLength(configDto.getLength())
                .setValid(configDto.isValid())
                .setDictionaries(mapDictionaries(configDto.getDictionaries()))
                .setConstant(configDto.getConstant())
                .setRandomGenerator(randomGenerator)
                .setTopics(configDto.getTopics().stream().map(Topic::safeFrom).toList())
                .setLang(configDto.getLanguage())
                .setRegexps(configDto.getRegexps());
    }

    private List<List<String>> mapDictionaries(Set<Set<String>> dictionaries) {
        if (CollectionUtils.isEmpty(dictionaries)) {
            return List.of();
        }

        return dictionaries.stream()
                .map(dictionary -> dictionary.stream()
                        .toList())
                .toList();
    }
}
