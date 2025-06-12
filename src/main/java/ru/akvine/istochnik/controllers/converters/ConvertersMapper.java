package ru.akvine.istochnik.controllers.converters;

import org.springframework.stereotype.Component;
import ru.akvine.istochnik.controllers.dto.converters.ConverterTypeDto;
import ru.akvine.istochnik.controllers.dto.converters.ConvertersListResponse;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.Arrays;

@Component
public class ConvertersMapper {
    public ConvertersListResponse convertToConvertersListResponse() {
        return new ConvertersListResponse()
                .setConverters(Arrays.stream(BaseType.values()).map(this::buildConverterDto).toList());
    }

    private ConverterTypeDto buildConverterDto(BaseType type) {
        return new ConverterTypeDto()
                .setType(type.getValue())
                .setSize(type.getSupportedConverterType().size())
                .setNames(type.getSupportedConverterType().stream()
                        .map(ConverterType::getName)
                        .toList());
    }
}
