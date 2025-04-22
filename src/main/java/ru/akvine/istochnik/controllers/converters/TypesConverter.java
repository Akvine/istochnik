package ru.akvine.istochnik.controllers.converters;

import org.springframework.stereotype.Component;
import ru.akvine.istochnik.controllers.dto.types.CustomTypeDto;
import ru.akvine.istochnik.controllers.dto.types.CustomTypesListResponse;
import ru.akvine.istochnik.enums.CustomType;

import java.util.Arrays;

@Component
public class TypesConverter {
    public CustomTypesListResponse convertToCustomTypesListResponse() {
        return new CustomTypesListResponse()
                .setTypes(Arrays.stream(CustomType.values()).map(this::buildCustomTypeDto).toList());
    }

    private CustomTypeDto buildCustomTypeDto(CustomType customType) {
        CustomTypeDto customTypeDto = new CustomTypeDto();
        customTypeDto.setCustomType(customType.getName());
        customTypeDto.setBaseType(customType.getBaseType().getValue());
        return customTypeDto;

    }
}
