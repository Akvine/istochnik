package ru.akvine.istochnik.controllers.converters;

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

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class GeneratorConverter {

    @Value("${randomizer.seed:#{null}}")
    private String seed;

    private static final String ALGORITHM_TYPE = "SHA1PRNG";
    private static final SecureRandom SECURE_RANDOM_GENERATOR = new SecureRandom();

    public GenerateData convertToGenerateData(GenerateTableRequest request) {
        Asserts.isNotNull(request);

        int size = request.getSize();
        String tableName = request.getTableName();
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
                    .setConfig(buildConfig(size, column.getConfig(), strategy))
                    .setGenerationStrategy(strategy)
                    .setErrorResolveInfo(column.getErrorResolveInfo())
                    .setConvertToString(column.isConvertToString())
                    .setPostConverters(
                            CollectionUtils.isEmpty(column.getPostConverters())
                                    ? List.of()
                                    : column.getPostConverters().stream()
                                            .map(this::buildConverter)
                                            .toList())
                    .setConverters(
                            CollectionUtils.isEmpty(column.getConverters())
                                    ? List.of()
                                    : column.getConverters().stream()
                                            .map(this::buildConverter)
                                            .toList()));
        }

        return new GenerateData()
                .setSize(size)
                .setGenerateColumns(generateColumns)
                .setFileType(FileType.from(request.getFileType()))
                .setTableName(tableName);
    }

    public ResponseEntity<?> convertToResponse(byte[] file, FileType fileType, String tableName) {
        Asserts.isNotNull(file);
        Asserts.isNotNull(fileType);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, fileType.getMimeType())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + tableName + fileType.getExtensionWithDot() + "\"")
                .body(file);
    }

    private Converter buildConverter(ConverterDto converterDto) {
        return new Converter()
                .setName(ConverterType.safeFrom(converterDto.getName()))
                .setArguments(converterDto.getArguments())
                .setProbability(converterDto.getProbability());
    }

    private Config buildConfig(int size, ConfigDto from, GenerationStrategy strategy) {
        SecureRandom randomGenerator;
        try {
             randomGenerator = resolveRandomizer(from);
        } catch (Exception exception) {
            randomGenerator = SECURE_RANDOM_GENERATOR;
        }

        Config to = new Config()
                .setSize(size)
                .setNotNull(from.isNotNull())
                .setUnique(from.isUnique())
                .setRangeType(
                        StringUtils.isBlank(from.getRangeType()) ? null : RangeType.from(from.getRangeType()))
                .setStart(from.getStart())
                .setEnd(from.getEnd())
                .setStep(from.getStep())
                .setLength(from.getLength())
                .setValid(from.isValid())
                .setDictionaries(mapDictionaries(from.getDictionaries()))
                .setConstant(from.getConstant())
                .setRandomGenerator(randomGenerator)
                .setTopics(from.getTopics().stream().map(Topic::safeFrom).toList())
                .setLang(from.getLanguage())
                .setRegexps(from.getRegexps())
                .setSeed(from.getSeed());

        // TODO: добавить инициализацию ConfigDto в провайдер в зависимости от RangeType / Типа данных
        if (GenerationStrategy.ALGORITHM == strategy && RangeType.SHIFT.getType().equalsIgnoreCase(from.getRangeType())) {
            String end = from.getEnd();
            Integer startInteger = Integer.parseInt(from.getStart());
            Integer stepInteger = Integer.parseInt(from.getStep());
            Integer endInteger;
            if (StringUtils.isBlank(end)) {
                endInteger = size * stepInteger + startInteger;

                if (endInteger < startInteger) {
                    Integer tmp = endInteger;
                    endInteger = startInteger;
                    startInteger = tmp;

                    to.setStart(String.valueOf(startInteger));
                    to.setStep(String.valueOf(Math.abs(stepInteger)));
                }

                to.setEnd(String.valueOf(endInteger));
            }

        }


        return to;
    }


    private List<List<String>> mapDictionaries(Set<Set<String>> dictionaries) {
        if (CollectionUtils.isEmpty(dictionaries)) {
            return List.of();
        }

        return dictionaries.stream()
                .map(dictionary -> dictionary.stream().toList())
                .toList();
    }

    private SecureRandom resolveRandomizer(ConfigDto configDto) throws NoSuchAlgorithmException {
        if (seed == null) {
            if (configDto.getSeed() == null) {
                return SECURE_RANDOM_GENERATOR;
            } else {
                SecureRandom randomGenerator = SecureRandom.getInstance(ALGORITHM_TYPE);
                randomGenerator.setSeed(configDto.getSeed().getBytes(StandardCharsets.UTF_8));
                return randomGenerator;
            }
        } else {
            SecureRandom randomGenerator = SecureRandom.getInstance(ALGORITHM_TYPE);
            randomGenerator.setSeed(seed.getBytes(StandardCharsets.UTF_8));
            return randomGenerator;
        }
    }
}
