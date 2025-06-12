package ru.akvine.istochnik.services.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.ConverterType;

@Data
@Accessors(chain = true)
public class Converter {
    private ConverterType name;

    private Object[] arguments;
}
