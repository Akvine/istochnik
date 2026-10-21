package ru.akvine.istochnik.controllers.converters;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.akvine.istochnik.controllers.dto.converters.ConverterExampleDto;
import ru.akvine.istochnik.controllers.dto.converters.ConverterInfoDto;
import ru.akvine.istochnik.controllers.dto.converters.ConverterTypeDto;
import ru.akvine.istochnik.controllers.dto.converters.ConvertersListResponse;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.ConverterType;
import ru.akvine.istochnik.enums.Language;
import ru.akvine.istochnik.services.MessageResolverService;

@Component
@RequiredArgsConstructor
public class ConvertersMapper {
    private final MessageResolverService messageResolverService;

    public ConvertersListResponse convertToConvertersListResponse(Language language) {
        return new ConvertersListResponse()
                .setConverters(Arrays.stream(BaseType.values())
                        .map(type -> buildConverterDto(type, language))
                        .toList());
    }

    private ConverterTypeDto buildConverterDto(BaseType type, Language language) {
        return new ConverterTypeDto()
                .setType(type.getValue())
                .setSize(type.getSupportedConverterType().size())
                .setNames(type.getSupportedConverterType().stream()
                        .map(converterType -> buildConverterInfoDto(converterType, language))
                        .toList());
    }

    private ConverterInfoDto buildConverterInfoDto(ConverterType converterType, Language language) {
        return new ConverterInfoDto()
                .setName(converterType.getName())
                .setDescription(messageResolverService.message(converterType.getDescriptionCode(), language))
                .setExample(buildExampleDto(converterType));
    }

    private ConverterExampleDto buildExampleDto(ConverterType converterType) {
        ConverterType.Example example = converterType.getExample();
        return new ConverterExampleDto()
                .setArguments(example.arguments())
                .setProbability(example.probability())
                .setValues(example.values())
                .setResults(example.results());
    }
}
