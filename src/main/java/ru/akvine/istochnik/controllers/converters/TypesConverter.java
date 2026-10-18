package ru.akvine.istochnik.controllers.converters;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.akvine.istochnik.controllers.dto.types.BaseTypeDto;
import ru.akvine.istochnik.controllers.dto.types.BaseTypesListResponse;
import ru.akvine.istochnik.controllers.dto.types.CustomTypeDto;
import ru.akvine.istochnik.controllers.dto.types.CustomTypesListResponse;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.Language;
import ru.akvine.istochnik.services.MessageResolverService;

@Component
@RequiredArgsConstructor
public class TypesConverter {
    private final MessageResolverService messageResolverService;

    public CustomTypesListResponse convertToCustomTypesListResponse(Language language) {
        return new CustomTypesListResponse()
                .setTypes(Arrays.stream(CustomType.values())
                        .map(customType -> buildCustomTypeDto(customType, language))
                        .toList());
    }

    public BaseTypesListResponse convertToBaseTypesListResponse(Language language) {
        return new BaseTypesListResponse()
                .setTypes(Arrays.stream(BaseType.values())
                        .map(baseType -> buildBaseTypeDto(baseType, language))
                        .toList());
    }

    private CustomTypeDto buildCustomTypeDto(CustomType customType, Language language) {
        CustomTypeDto customTypeDto = new CustomTypeDto();
        customTypeDto.setCustomType(customType.getName());
        customTypeDto.setBaseType(customType.getBaseType().getValue());
        customTypeDto.setDescription(messageResolverService.message(customType.getCode(), language));
        customTypeDto.setExample(customType.getExample());
        return customTypeDto;
    }

    private BaseTypeDto buildBaseTypeDto(BaseType baseType, Language language) {
        return new BaseTypeDto()
                .setBaseType(baseType.getValue())
                .setDescription(messageResolverService.message(baseType.getCode(), language));
    }
}
