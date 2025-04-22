package ru.akvine.istochnik.controllers.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.akvine.istochnik.controllers.dto.types.BaseTypeDto;
import ru.akvine.istochnik.controllers.dto.types.BaseTypesListResponse;
import ru.akvine.istochnik.controllers.dto.types.CustomBaseTypeDto;
import ru.akvine.istochnik.controllers.dto.types.CustomTypesListResponse;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.Language;
import ru.akvine.istochnik.services.MessageResolverService;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class TypesConverter {
    private final MessageResolverService messageResolverService;

    public CustomTypesListResponse convertToCustomTypesListResponse() {
        return new CustomTypesListResponse()
                .setTypes(Arrays.stream(CustomType.values()).map(this::buildCustomTypeDto).toList());
    }

    public BaseTypesListResponse convertToBaseTypesListResponse(Language language) {
        return new BaseTypesListResponse()
                .setTypes(Arrays.stream(BaseType.values())
                        .map(baseType -> buildBaseTypeDto(baseType, language))
                        .toList());
    }

    private CustomBaseTypeDto buildCustomTypeDto(CustomType customType) {
        CustomBaseTypeDto customTypeDto = new CustomBaseTypeDto();
        customTypeDto.setCustomType(customType.getName());
        customTypeDto.setBaseType(customType.getBaseType().getValue());
        return customTypeDto;
    }

    private BaseTypeDto buildBaseTypeDto(BaseType baseType, Language language) {
        return new BaseTypeDto()
                .setBaseType(baseType.getValue())
                .setDescription(messageResolverService.message(baseType.getCode(), language));
    }
}
